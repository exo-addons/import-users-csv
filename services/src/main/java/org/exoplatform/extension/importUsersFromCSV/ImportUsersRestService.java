package org.exoplatform.extension.importUsersFromCSV;

import com.google.caja.util.Sets;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.*;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.service.rest.RestChecker;
import org.exoplatform.social.service.rest.Util;
import org.exoplatform.webui.exception.MessageException;
import org.gatein.common.p3p.P3PConstants;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static java.util.Objects.nonNull;


@Path("/importusersrest")
@Produces("application/json")
public class ImportUsersRestService implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(ImportUsersRestService.class);
    private static final String portalContainerName = "portal";
    private static final String[] SUPPORTED_FORMATS = new String[]{"json"};
    private static Boolean requestStarted = false;
    private OrganizationService orgService_;
    private SpaceService spaceService_;

    public ImportUsersRestService(OrganizationService orgService, SpaceService spaceService) {
        this.orgService_=orgService;
        this.spaceService_=spaceService;
    }

    @POST
    @Path("importusers")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response importUsers(@Context HttpServletRequest request,
                                @Context HttpServletResponse response,
                                @Context UriInfo uriInfo,
                                @QueryParam("creatduplicated") Boolean creatDuplicated,
                                @QueryParam("addexistingusers") Boolean addExistingUsers,
                                List<UserBean> users) {

        // report String in csv format, status can be created, updated, duplicated, error+message;
        String csv = "username,lastname,firstname,email,status" + "\n";
        Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
        UserHandler uh = orgService_.getUserHandler();
        MembershipTypeHandler mtHandler = orgService_.getMembershipTypeHandler();
        GroupHandler gHandler = orgService_.getGroupHandler();
        MembershipHandler mHandler = orgService_.getMembershipHandler();
        MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
        try {
            if(sourceIdentity == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            int i=0;
            int j=0;
            startRequest();
            for(UserBean userIn:users) {
                User user=null;

                //define status, possible value : created, updated, duplicated, error+message
                String status = "Created";
                String name = userIn.getUserName();
                name = name.toLowerCase();
                String firstName = userIn.getFirstName();
                String lastName = userIn.getLastName();
                String email = userIn.getEmail();

                // add try catch in the loop to continue the import if a import of user failed
                try {

                    boolean exist = false;
                    boolean ch = true;
                    while (ch == true) {
                        ch = false;
                        if (!isLowerCaseLetterOrDigit(name.charAt(name.length() - 1))) {
                            name = name.substring(0, name.length() - 1);
                            ch = true;
                        }
                        if (!isLowerCaseLetter(name.charAt(0))) {
                            name = name.substring(1);
                            ch = true;
                        }
                    }
                    name = name.replace(" ", "");
                    // Before Creating user, chek if the userName and the email are alredy used or not
                    Query query = new Query();
                    query.setEmail(userIn.getEmail());

                    if (uh.findUsersByQuery(query).getSize() > 0) {
                        user = uh.findUsersByQuery(query).load(0, 1)[0];
                        if (user.getUserName().equals(name))
                        {
                            status = "Updated";
                            exist = true;
                            i++;
                        }
                        else {
                            LOG.warn(userIn.getEmail() + " not created mail already existed with username : "+ user.getUserName());
                            status = "Not created mail already existed with username : "+ user.getUserName();
                            user = null;
                        }

                    } else if (uh.findUserByName(name) != null) {

                        if (creatDuplicated != null && creatDuplicated) {
                            int suffix = 1;
                            String newName = name + suffix;
                            while (uh.findUserByName(newName) != null) {
                                suffix++;
                                newName = name + suffix;
                            }
                            user = uh.createUserInstance(newName);
                            user.setDisplayName(userIn.getFirstName() + " " + userIn.getLastName());
                            user.setPassword(userIn.getPassword());
                            user.setEmail(userIn.getEmail());
                            user.setLastName(userIn.getLastName());
                            user.setFirstName(userIn.getFirstName());

                            uh.createUser(user, true);


                            saveUserGateinProfile(newName, userIn.getAdditionalInformations());

                            i++;
                            j++;
                            LOG.info("User " + userIn.getFirstName() + userIn.getLastName() + " imported");
                            if (j < 20) {
                                endRequest();
                                startRequest();
                                j = 0;
                            }
                            status = "Duplicated";
                            exist = true;
                        } else {
                            user = uh.findUserByName(name);
                            LOG.warn(name + " not created : username already existed with an another email");
                            user = null;
                            status = "Not created : username already existed with an another email";
                        }

                    } else {
                        user = uh.createUserInstance(name);
                        user.setDisplayName(userIn.getFirstName() + " " + userIn.getLastName());
                        user.setPassword(userIn.getPassword());
                        user.setEmail(userIn.getEmail());
                        user.setLastName(userIn.getLastName());
                        user.setFirstName(userIn.getFirstName());
                        uh.createUser(user, true);

                        saveUserGateinProfile(name, userIn.getAdditionalInformations());

                        i++;
                        j++;
                        LOG.info("User " + userIn.getFirstName() + userIn.getLastName() + " imported");
                        if (j < 20) {
                            endRequest();
                            startRequest();
                            j = 0;
                        }
                        exist = true;
                    }


                    updateSocialeProfile(userIn);


                    if (exist && addExistingUsers != null && addExistingUsers) {

                        // Add users to groups
                        Boolean groupUpdated = false;
                        if (userIn.getGroups() != null && !userIn.getGroups().equals("")) {
                            String[] groups = userIn.getGroups().split(";");
                            for (String membership : groups) {
                                String[] groupString = membership.split(":");
                                String membershipTypeId = "";
                                String groupId = "";
                                if (membership.contains(":")) {
                                    membershipTypeId = groupString[0];
                                    groupId = groupString[1];
                                } else {
                                    membershipTypeId = "member";
                                    groupId = groupString[0];

                                }
                                MembershipType membershipType = mtHandler.findMembershipType(membershipTypeId);
                                if (membershipType == null) {
                                    LOG.warn("Membership type with id =" + membershipTypeId + " not found");
                                } else {
                                    Group group = gHandler.findGroupById(groupId);
                                    if (group != null) {
                                        if (mHandler.findMembershipByUserGroupAndType(user.getUserName(),group.getId(),membershipType.getName())==null) {
                                            mHandler.linkMembership(user, group, membershipType, true);
                                            groupUpdated = true;
                                        }
                                    } else {
                                        LOG.warn("Group with id =" + groupId + " not found");
                                    }
                                }
                            }
                        }

                        // Add users to spaces
                        if (userIn.getSpaces() != null && !userIn.getSpaces().equals("")) {
                            String[] spaces = userIn.getSpaces().split(";");
                            for (String spaceId : spaces) {
                                Space space = spaceService_.getSpaceByPrettyName(spaceId);
                                if (space != null) {
                                    if (!spaceService_.isMember(space,user.getUserName())) {
                                        spaceService_.addMember(space, user.getUserName());
                                        groupUpdated = true;
                                    }
                                } else {
                                    LOG.warn("Space  =" + spaceId + " not found");
                                }

                            }
                        }
                        if (groupUpdated && status.equals("Updated")) status += " - Space or group updated";
                    }

            }catch (Exception e){
                LOG.error(e);
                // set error flag + exception
                status = "Error-"+e.getMessage();
                }
                // if user not null update report information with value coming form user instance
            if (user!=null)
                {
                    name = user.getUserName();
                    firstName = user.getFirstName();
                    lastName = user.getLastName();
                    email = user.getEmail();
                }
                csv+= name+","+lastName+","+firstName+","+email+","+status+ "\n";
            }
            File temp = null;
            temp = File.createTempFile("imported-users", ".tmp");

            // write to it
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(csv);
            bw.close();
            JSONObject jsonGlobal = new JSONObject();
            jsonGlobal.put("message",i+" Users Imported)");
            jsonGlobal.put("file",temp.getAbsolutePath());
            return Response.ok(jsonGlobal.toString(), mediaType).build();
        } catch (Exception e) {
            LOG.error(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An internal error has occured").build();
        }finally{
            endRequest();
        }
    }

    private void saveUserGateinProfile(String userName,  Map<String, String> userAdditionalInformationsToSave) throws Exception {
        UserProfileHandler handler = orgService_.getUserProfileHandler();
        UserProfile userProfile = handler.findUserProfileByName(userName);
        if (userProfile == null) {
            userProfile = handler.createUserProfileInstance(userName);
        }

        if (null != userAdditionalInformationsToSave) {
            for (Map.Entry additionalInformationsEntry : userAdditionalInformationsToSave.entrySet()) {
                userProfile.setAttribute((String) additionalInformationsEntry.getKey(), (String) additionalInformationsEntry.getValue());
            }
        }
        handler.saveUserProfile(userProfile, true);
    }

    private void updateSocialeProfile(UserBean user) throws MessageException {

        Profile socialProfile = CommonsUtils.getService(IdentityManager.class).getOrCreateIdentity("organization", user.getUserName(), true).getProfile();
        for (Map.Entry mapEntry : user.getAdditionalInformations().entrySet()) {

            String key = (String) mapEntry.getKey();
            String value = (String) mapEntry.getValue();
            socialProfile.setProperty(key, value);

            List<Profile.UpdateType> list = new ArrayList<>();
            list.add(Profile.UpdateType.CONTACT);
            socialProfile.setListUpdateTypes(list);
        }
        CommonsUtils.getService(IdentityManager.class).updateProfile(socialProfile);
    }

    @GET
    @Path("getReport")
    @Produces("application/vnd.ms-excel")
    public Response getReport(@QueryParam("reportId") String reportId) {
        try {
            File temp = null;

            temp = new File(reportId);

            // delete temporary file when the program is exited
            temp.deleteOnExit();

            Response.ResponseBuilder response = Response.ok((Object) temp);
            response.header("Content-Disposition", "attachment; filename=imported-users.csv");
            return response.build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private static boolean isLowerCaseLetterOrDigit(char character) {
        return Character.isDigit(character) || (character >= 'a' && character <= 'z');
    }

    private static boolean isLowerCaseLetter(char character) {
        return (character >= 'a' && character <= 'z');
    }

    private void endRequest() {
        if (requestStarted && orgService_ instanceof ComponentRequestLifecycle) {
            try {
                ((ComponentRequestLifecycle) orgService_).endRequest(PortalContainer.getInstance());
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
            requestStarted = false;
        }
    }

    private void startRequest() {
        if (orgService_ instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) orgService_).startRequest(PortalContainer.getInstance());
            requestStarted = true;
        }
    }

}
