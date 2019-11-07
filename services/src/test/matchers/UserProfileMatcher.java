package matchers;

import org.exoplatform.services.organization.UserProfile;
import org.mockito.ArgumentMatcher;

import java.util.Map;

public class UserProfileMatcher implements ArgumentMatcher<UserProfile> {

    private UserProfile expected;

    public UserProfileMatcher(UserProfile expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(UserProfile userProfile) {

        boolean usernameEquals = expected.getUserName().equals(userProfile.getUserName());
        boolean mapSizeEquals = expected.getUserInfoMap().size() == userProfile.getUserInfoMap().size();

        for (Map.Entry<String, String> userInfo :expected.getUserInfoMap().entrySet()) {
            String key = userInfo.getKey();
            String value = userInfo.getValue();

            if (!userProfile.getUserInfoMap().containsKey(key) || !userProfile.getUserInfoMap().get(key).equals(value)) {
                return false;
            }
        }

        return usernameEquals && mapSizeEquals;
    }
}