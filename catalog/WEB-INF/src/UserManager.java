
import java.util.Map;
import java.util.HashMap;
import java.lang.IllegalStateException;
import java.security.InvalidParameterException;
import java.lang.IllegalArgumentException;
import java.util.logging.Logger;

public class UserManager {

 private Map<String, User> users = new HashMap<>();
 private final static Logger log = Logger.getLogger(UserManager.class.getName());

 public UserManager(Map<String, User> users) {
  this.users = users;
 }

 public Map<String, User> getUsers() {
  return this.users;
 }

 public void registerUser(User userToRegister) {
  log.info("Beginning of user registration...");
  if (userToRegister == null || userToRegister.toString().isBlank()) {
   throw new InvalidParameterException("Provided parameter is null.");
  }

  if (this.users.containsValue(userToRegister)) {
   log.warning("User already exists.");
   throw new IllegalStateException("User already exists within the map");
  }
  
  // Add user to users map
  this.users.put(userToRegister.getEmail(), userToRegister);
  log.info("User was successfully registered.");
 }

 public User loginUser(User userToLogin) {
  log.info("Trying to log in...");

  // Capture and validate credentials of user from parameters
  String emailToLogin = getStringOrCatch(userToLogin.getEmail());
  String passwordToLogin = getStringOrCatch(userToLogin.getPassword());

  boolean isUserExists = this.users.containsValue(userToLogin);
  User loggedInUser = null;

  if (!isUserExists) {
   log.warning("Trying to log in with unknown credentials");
   throw new IllegalArgumentException("User does not exists within map");
  } else {
   log.info("Beginning of user validation...");
   loggedInUser = users.entrySet().stream()
                .filter(e -> e.getValue().equals(userToLogin))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
   if (loggedInUser != null) {

    // Capture credentials from existing user
    String registeredEmail = loggedInUser.getEmail();
    String registeredPassword = loggedInUser.getPassword();

    // Compare credentials of user existing within map and user from parameter
    boolean isEmailEqual = registeredEmail.equals(emailToLogin);
    boolean isPasswordEqual = registeredPassword.equals(passwordToLogin);


    if (!isEmailEqual || !isPasswordEqual) {
     log.warning("Some credentials are not equal: ");
     if (!isEmailEqual && !isPasswordEqual) {
      log.warning("Both email and password are not equal");
     } else {
      String wrongCredential = isEmailEqual ? "Password is wrong" : "Email is wrong";
      log.warning(wrongCredential);
     }
     throw new IllegalStateException("Wrong email or password");
    }
    log.info("User successfully logged in.");
    return loggedInUser;
   }
   log.info("User not logged in");
   return null;
  }
 }

 // String validation
 private String getStringOrCatch(String value) {
  if (value == null || value.isBlank()) {
   throw new InvalidParameterException(value + " is null");
  }
  return value;
 }

}
