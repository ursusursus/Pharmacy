package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class User extends Model {

	@Id
	public Long id;

	//@Required
	public String email;

	// tu ak dam @Required tak sa nemozem prihlasit lebo 
	// asi ocakava aby tam bolo aj meno
	public String name;

	//@Required
	public String password;
	
	// to iste
	public String role;
	
	@OneToMany
	public List<Drug> drugs;

	public static Model.Finder<String, User> find = new Model.Finder(String.class, User.class);

	public static List<User> findAll() {
		return find.all();
	}

	public static User findById(Long id) {
		return find.where().eq("id", id).findUnique();
	}

	public static User authenticate(String email, String password) {
		return find.where().eq("email", email).eq("password", password).findUnique();
	}

}
