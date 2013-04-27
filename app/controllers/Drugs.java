package controllers;

import java.util.Arrays;
import java.util.List;

import models.Drug;
import models.User;
import play.data.Form;
import static play.data.Form.form;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Security;
import utils.Role;
import views.html.*;

public class Drugs extends Controller {

	static Form<Drug> drugForm = Form.form(Drug.class);

	public static Result show(Long id) {
		String userId = session("user_id");
		if (userId == null) {
			System.out.println("You need to login first");
			return redirect(routes.Application.login());
		}

		Drug drug = Drug.findById(id);
		return ok(drug_show.render(drug));
	}

	/**
	 * Action "/drugs/newDrug"
	 * 
	 * @return
	 */
	public static Result create() {

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

		// Serve form
		return ok(drug_create.render(drugForm));
	}

	/**
	 * Action "/drugs/create"
	 * 
	 * @return
	 */
	public static Result save() {

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

		// Is form valid?
		Form<Drug> filledForm = drugForm.bindFromRequest();
		if (!isValid(filledForm)) {
			return redirect(routes.Drugs.create());
		}

		// Add new drug
		Drug drug = filledForm.get();
		drug.user = User.findById(Long.parseLong(userId));
		Drug.addDrug(drug);
		flash("success", "Drug " + drug.name + " created successfully");

		return redirect(routes.Application.home());

	}
	
	public static Result edit(Long id) {
		drugForm.fill(Drug.findById(id));
		return ok(drug_edit.render(id, drugForm));
	}
	
	public static Result update(Long id) {
		Form<Drug> filledForm = drugForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            // return badRequest(editForm.render(id, computerForm));
        }
        Drug drug = filledForm.get();
        drug.update(id);
        
        flash("success", "Drug " + drug.name + " has been updated");
        return redirect(routes.Application.home());
    }

	public static Result delete(Long id) {
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
		
		Drug drug = Drug.findById(id);
		drug.delete();
		flash("success", "Drug " + drug.name + " deleted successfully");
		
		return redirect(routes.Application.home());
	}
	
	
	
	
	public static Result search() {
		String key = form().bindFromRequest().get("key");
		List<Drug> drugsByName = Drug.findByName(key);
		List<Drug> drugsByActiveIngredient = Drug.findByActiveIngredient(key);
		
		return ok(search.render(drugsByName, drugsByActiveIngredient));
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
