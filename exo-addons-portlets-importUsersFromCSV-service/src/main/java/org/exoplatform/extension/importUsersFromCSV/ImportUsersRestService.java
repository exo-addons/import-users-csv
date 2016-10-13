package org.exoplatform.extension.importUsersFromCSV;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.organization.*;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.RestChecker;
import org.exoplatform.social.service.rest.Util;
import org.exoplatform.social.core.manager.IdentityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.JSONObject;


@Path("/importusersrest")
@Produces("application/json")
public class ImportUsersRestService implements ResourceContainer {


    private static final Log LOG = ExoLogger.getLogger(ImportUsersRestService.class);
    private static final String portalContainerName = "portal";
    private static final String[] SUPPORTED_FORMATS = new String[]{"json"};
    private static Boolean requestStarted = false;
    private OrganizationService orgService_;

    private IdentityManager identityManager_;

    public ImportUsersRestService(OrganizationService orgService, IdentityManager identityManager) {
        this.orgService_=orgService;
        this.identityManager_=identityManager;
    }

    @POST
    @Path("importusers")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response importUsers(@Context HttpServletRequest request,@Context HttpServletResponse response,@Context UriInfo uriInfo,
                                List<UserBean> users) throws Exception {

        Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
        UserHandler uh = orgService_.getUserHandler();
        MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
        try {
            identityManager_=Util.getIdentityManager(portalContainerName);
            if(sourceIdentity == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            User user_=null;
            int i=0;
            int j=0;
            startRequest();
            for(UserBean userIn:users)
            {

                String name=userIn.getUserName();
                name=  name.toLowerCase();
                boolean ch=true;
                while (ch==true)
                {
                    ch=false;
                    if (!isLowerCaseLetterOrDigit(name.charAt(name.length()-1)))
                    {
                        name = name.substring(0, name.length()-1);
                        ch=true;
                    }
                    if (!isLowerCaseLetter(name.charAt(0))) {
                        name =name.substring(1);
                        ch=true;
                    }
                }
                name = name.replace(" ","");
                // Before Creating user, chek if the userName and the email are alredy used or not
                Query query = new Query();
                query.setEmail(userIn.getEmail());
                if (uh.findUserByName(name) != null) {
                    user_ =uh.findUserByName(name);
                    LOG.warn(name+" user name already exists, User will not be Created");
                }

                else if (uh.findUsersByQuery(query).getSize() > 0) {
                    user_ =uh.findUsersByQuery(query).load(0,1)[0];
                    LOG.warn(userIn.getEmail() + " already exists, User will not be Created");

                }else {
                    User user = uh.createUserInstance(name);
                    user.setDisplayName(userIn.getFirstName());
                    user.setPassword(name);
                    user.setEmail(userIn.getEmail());
                    user.setLastName(userIn.getLastName());
                    user.setFirstName(userIn.getFirstName());
                    uh.createUser(user, true);
                    i++;j++;
                    LOG.info("User " + userIn.getFirstName() + userIn.getLastName() + " imported");
                    if(j<20){
                        endRequest();
                        startRequest();
                        j=0;
                    }
                }
            }
            JSONObject jsonGlobal = new JSONObject();
            jsonGlobal.put("message",i+" Users Imported");
            return Response.ok(jsonGlobal.toString(), mediaType).build();
        } catch (Exception e) {
            LOG.error(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An internal error has occured").build();
        }finally{
            endRequest();
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

