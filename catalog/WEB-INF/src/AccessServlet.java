
import java.util.Enumeration;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

public class AccessServlet extends HttpServlet {

 private UserManager userManager;
 private DatabaseManager db;
 private static final long serialVersionUID = 1L;

 @Override
 public void init() {
  this.db = new DatabaseManager();
  Map<String, User> users = this.db.getUsers();
  this.userManager = new UserManager(users);
 }

 @Override
 public void destroy() {
  this.db.writeUsers(this.userManager.getUsers());
 }

 @Override
 public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  String action = request.getParameter("action");
  if (action == null) {
   response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action is null");
   return;
  }

  switch (action) {
   case "login" -> {
     loginAction(request, response);
     return;
   }
   case "register" -> {
      registerAction(request, response);
      return;
   }
   case "logout" -> {
     logout(request, response);
     return;
   }
   default -> {
     response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action provided in request");
     return;
   }
  }
 }

 private void loginAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
  String emailFromParameter = request.getParameter("email");
  String password = request.getParameter("password");
  var currentSession = request.getSession();
  if (currentSession != null) {
   System.out.println("Current session exists");
   String emailAttribute = (String) currentSession.getAttribute("email");
   if (emailAttribute != null) {
    if (emailFromParameter.equals(emailAttribute)) {
     response.sendRedirect("/catalog/catalog.html");
     return;
    } else {
     invalidateSessionAndRedirect(currentSession, response);
     return;
    }
   }
  }
   User userToLogin = new User(emailFromParameter, password);
   try {
      this.userManager.loginUser(userToLogin);
   } catch (Exception e) {
      String errorResponse = "Looks like there was an error with the user you tried to log in. Make sure that all the fields in the form have some value and are not empty.";
      System.out.println(errorResponse);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorResponse);
      return;
   } finally {
      currentSession.setAttribute("email", userToLogin.getEmail());
      System.out.println("Session was successfuly updated for current user");
      response.sendRedirect("/catalog/catalog.html");
   }
 }


 private void registerAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
  String email = request.getParameter("email");
  String password = request.getParameter("password");
  String firstName = request.getParameter("firstName");
  String lastName = request.getParameter("lastName");
  var currentSession = request.getSession();
  if (currentSession != null) {
   String sessionEmail = (String) currentSession.getAttribute("email");
   String sessionPassword = (String) currentSession.getAttribute("password");
   if (email.equals(sessionEmail) && password.equals(sessionPassword)) {
      currentSession.invalidate();
      String errorResponse = "Already registered user: not allowed to register twice.";
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorResponse);
      return;
   }
  }
  User userToRegister = new User(email, password, firstName, lastName);
  try {
   this.userManager.registerUser(userToRegister);
  } catch (Exception e) {
   response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to register user. Stack trace: " + e);
   return;
  }
  response.sendRedirect("/catalog/login.html");
 }

 private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
  var session = request.getSession();
  if (session == null) {
   response.sendRedirect("/catalog/login.html");
  } else {
   session.invalidate();
   response.sendRedirect("/catalog/login.html");
  }
 }


 private void invalidateSessionAndRedirect(HttpSession session, HttpServletResponse response) throws IOException {
  session.invalidate();
  response.sendRedirect("/catalog/login.html");
 }

}
