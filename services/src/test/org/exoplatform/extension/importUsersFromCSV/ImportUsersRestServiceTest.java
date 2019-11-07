package org.exoplatform.extension.importUsersFromCSV;

import matchers.UserProfileMatcher;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.organization.impl.UserProfileImpl;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.gatein.common.p3p.P3PConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportUsersRestServiceTest {

    private ImportUsersRestService importUsersRestService;

    @Mock
    private SpaceService mockSpaceService;
    @Mock
    private IdentityManager mockIdentityManager;
    @Mock
    private UserProfileHandler mockUserProfileHandler;
    @Mock
    private OrganizationService mockOrganizationService;

    @Before
    public void setUp() {
        importUsersRestService = new ImportUsersRestService(mockOrganizationService, mockSpaceService, mockIdentityManager);

        when(mockOrganizationService.getUserProfileHandler()).thenReturn(mockUserProfileHandler);
    }

    @Test
    public void should_create_user_profile_when_not_exist() throws Exception {

        User MARC = new UserImpl();
        String MARC_USERNAME = "MARC";
        MARC.setUserName(MARC_USERNAME);

        Map<String, String> MARC_ADDITIONAL_INFORMATIONS = new HashMap<>();

        given(mockUserProfileHandler.findUserProfileByName(MARC_USERNAME)).willReturn(null);
        when(mockUserProfileHandler.createUserProfileInstance(MARC_USERNAME)).thenReturn(new UserProfileImpl());

        importUsersRestService.createOrUpdateUserProfile(MARC, MARC_ADDITIONAL_INFORMATIONS);

        verify(mockUserProfileHandler).createUserProfileInstance(MARC_USERNAME);
    }

    @Test
    public void should_save_user_profile_availables_keys() throws Exception {

        User MARC = new UserImpl();
        String MARC_USERNAME = "MARC";
        MARC.setUserName(MARC_USERNAME);

        Map<String, String> MARC_ADDITIONAL_INFORMATIONS = new HashMap<>();
        MARC_ADDITIONAL_INFORMATIONS.put(P3PConstants.INFO_USER_JOB_TITLE, "developer"); // available
        MARC_ADDITIONAL_INFORMATIONS.put(P3PConstants.INFO_USER_BUSINESS_INFO_POSTAL_POSTALCODE, "35000"); // available
        MARC_ADDITIONAL_INFORMATIONS.put(P3PConstants.INFO_USER_BDATE, "06/12/2015"); // not available

        doNothing().when(mockUserProfileHandler).saveUserProfile(any(), anyBoolean());
        UserProfileImpl EXISTING_MARC_PROFILE = new UserProfileImpl();
        EXISTING_MARC_PROFILE.setUserName(MARC_USERNAME);
        given(mockUserProfileHandler.findUserProfileByName(MARC_USERNAME)).willReturn(EXISTING_MARC_PROFILE);

        importUsersRestService.createOrUpdateUserProfile(MARC, MARC_ADDITIONAL_INFORMATIONS);

        UserProfileImpl expectedMarcUserProfile = new UserProfileImpl();
        expectedMarcUserProfile.setUserName(MARC_USERNAME);
        expectedMarcUserProfile.setAttribute(P3PConstants.INFO_USER_JOB_TITLE, "developer");
        expectedMarcUserProfile.setAttribute(P3PConstants.INFO_USER_BUSINESS_INFO_POSTAL_POSTALCODE, "35000");

        verify(mockUserProfileHandler).saveUserProfile(argThat(new UserProfileMatcher(expectedMarcUserProfile)), eq(true));
    }
}