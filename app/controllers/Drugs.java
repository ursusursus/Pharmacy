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
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
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
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
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
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			return redirect(routes.Application.login());
		}

		Form<Drug> filledForm = drugForm.bindFromRequest();
		// filledForm.hasErrors();

		// Add new drug
		Drug drug = filledForm.get();
		drug.user = User.findById(Long.parseLong(userId));
		Drug.addDrug(drug);

		flash("success", "Drug " + drug.name + " created successfully");
		return redirect(routes.Application.home());

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Result edit(Long id) {
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			return redirect(routes.Application.login());
		}

		// Form je immutable takze preto v paramsi alebo robit novy objekt
		return ok(drug_edit.render(id, drugForm.fill(Drug.findById(id))));
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Result update(Long id) {
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			return redirect(routes.Application.login());
		}


		Form<Drug> filledForm = drugForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			// return badRequest(editForm.render(id, computerForm));
		}

		// Update
		Drug drug = filledForm.get();
		Drug.updateDrug(drug, id);

		flash("success", "Drug " + drug.name + " has been updated");
		return redirect(routes.Application.home());
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Result delete(Long id) {
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			return redirect(routes.Application.login());
		}


		// Delete
		Drug drug = Drug.findById(id);
		Drug.deleteDrug(drug);

		flash("success", "Drug " + drug.name + " deleted successfully");
		return redirect(routes.Application.home());
	}

	/**
	 * 
	 * @return
	 */
	public static Result search() {
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			return redirect(routes.Application.login());
		}


		String key = form().bindFromRequest().get("key");
		List<Drug> drugsByName = Drug.findByName(key);
		List<Drug> drugsByActiveIngredient = Drug.findByActiveIngredient(key);

		return ok(search.render(drugsByName, drugsByActiveIngredient));
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Result sell(Long id) {
		String userId = session("user_id");
		if (!Security.isAuthenticated(userId)) {
			return redirect(routes.Application.login());
		}
		
		String userRole = session("user_role");
		if (!Security.isAuthorized(Arrays.asList(Role.ADMIN, Role.WAREHOUSEMAN), userRole)) {
			return redirect(routes.Application.login());
		}


		Drug drug = Drug.findById(id);
		Drug.sellDrug(drug);

		flash("success", "Drug " + drug.name + " sold successfully");
		return redirect(routes.Application.home());
	}


	/**
	 * 
	 * @param form
	 * @return
	 */
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
