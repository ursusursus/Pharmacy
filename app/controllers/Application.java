package controllers;

import java.util.Arrays;
import java.util.List;

import models.Drug;
import models.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Role;
import utils.Security;
import views.html.*;

public class Application extends Controller {

	static Form<User> userForm = Form.form(User.class);
	static Form<Drug> drugForm = Form.form(Drug.class);
	static Form<Drug> orderDrugForm = Form.form(Drug.class);

	public static Result index() {
		//session().clear();
		return redirect(routes.Application.home());
	}

	public static Result login() {
		return ok(login.render(userForm));
	}

	public static Result logout() {
		flash("success", "You've been logged out");
		session().clear();
		return redirect(routes.Application.login());
	}
	

	public static Result register() {
		return ok(register.render(userForm));
	}
	

	public static Result createUser() {
		Form<User> filledForm = userForm.bindFromRequest();
		if (!isValid(filledForm)) {
			return redirect(routes.Application.register());
		}

		User user = filledForm.get();
		user.save();

		flash("success", "Registration successful");
		return redirect(routes.Application.home());
	}
	

	public static Result authenticate() {
		System.out.println("authenticate");

		Form<User> filledForm = userForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(login.render(filledForm));
		}

		User user = filledForm.get();
		User authenticatedUser = User.authenticate(user.email, user.password);
		if (authenticatedUser == null) {
			flash("error", "Wrong username or password");
			System.out.println("wrong username or password");
			return badRequest(login.render(filledForm));
		}

		session("user_id", String.valueOf(authenticatedUser.id));
		session("user_role", authenticatedUser.role);
		return redirect(routes.Application.home());
	}
	

	public static Result home() {
		//session().clear();
		String userId = session("user_id");
		if (userId == null) {
			System.out.println("You need to login first");
			return redirect(routes.Application.login());
		}

		User loggedUser = User.findById(Long.parseLong(userId));
		List<Drug> allDrugs = Drug.findAll();
		List<Drug> missingDrugs = Drug.findMissingDrugs();

		return ok(home.render(loggedUser, allDrugs, missingDrugs, drugForm));
	}
	

	public static Result orderMissingDrugs() {
		// Is authenticated?
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			System.out.println("You need to login first");
			return redirect(routes.Application.login());
		}

		// Is authorized?
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			System.out.println("Unauthorized access");
			return redirect(routes.Application.login());
		}
		
		List<Drug> orderingDrugs = Drug.findOrderingDrugs();

		User loggedUser = User.findById(Long.parseLong(userId));
		
		return ok(order.render(loggedUser,orderingDrugs));
	}

	private static <T extends Model> boolean isValid(Form<T> form) {
		if (form.hasErrors()) {
			for (String key : form.errors().keySet()) {
				List<ValidationError> currentError = form.errors().get(key);
				for (ValidationError error : currentError) {
					// flash(key, error.message());
					print(key + " - " + error.message());
				}
			}
			return false;
		}
		return true;
	}

	private static void print(String message) {
		System.out.println(message);
	}

}
