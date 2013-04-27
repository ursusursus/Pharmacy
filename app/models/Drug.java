package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Drug extends Model {

	@Id
	public Long id;

	@Required
	public String name;
	
	@Required
	public Integer quantity;
	
	@Required
	public String price;
	
	@Required
	public String description;
	
	@Required
	public String activeIngredient;
	
	public Integer isPrescribed;
	
	@ManyToOne
	public User user;

	public static Finder<Long, Drug> find = new Finder(Long.class, Drug.class);

	public static List<Drug> findAll() {
		return find.all();
	}
	
	public static Drug findById(Long id) {
		return find.byId(id);
	}
	
	public static List<Drug> findByActiveIngredient(String activeIngredient) {
		return find.where().eq("activeIngredient", activeIngredient).findList();
	}
	
	public static List<Drug> findMissingDrugs() {
		return find.where().le("quantity", 0).findList();
	}
	
	public static Drug findByName(String name) {
		return find.where().le("name", 0).findUnique();
	}
	
	public static void addDrug(Drug drug) {
		drug.save();
	}
	
	public static void addNewDrugs() {
		
	}
	
	public static void editDrug(Drug drug) {
		
	}
	
	public static void deleteDrug(Drug drug) {
		drug.delete();
	}
	
	public static void sellDrug(Drug drug) {
		
	}
	


}
