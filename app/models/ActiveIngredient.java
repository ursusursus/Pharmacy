package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class ActiveIngredient extends Model {
	
	@Id
	public Long id;
	
	@Required
	public String name;

	//ManyToMany vztah s liekom

}
