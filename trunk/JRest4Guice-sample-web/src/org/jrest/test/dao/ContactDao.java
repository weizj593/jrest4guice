package org.jrest.test.dao;

import java.util.List;

import org.jrest.dao.annotations.Create;
import org.jrest.dao.annotations.Dao;
import org.jrest.dao.annotations.Delete;
import org.jrest.dao.annotations.Find;
import org.jrest.dao.annotations.Retrieve;
import org.jrest.dao.annotations.Update;
import org.jrest.dao.annotations.Find.FirstResult;
import org.jrest.dao.annotations.Find.MaxResults;
import org.jrest.test.entity.Contact;

import com.google.inject.name.Named;

/**
 * 负责联人系持久化处理的DAO
 * @author cnoss
 */
@Dao
public interface ContactDao {
	@Create
	public void createContact(Contact contact);

	@Find(namedQuery="list")
	public List<Contact> listContacts(@FirstResult int first,@MaxResults int max);
	
	@Find(namedQuery="byDate")
	public List<Contact> listContactsByDate(@Named("changeDate") Object time);

	@Find(query="select e from Contact e where e.name=:name")
	public List<Contact> findContactByName(@Named("name") String name);

	@Retrieve
	public Contact findContactById(String contactId);
	
	@Update
	public void updateContact(Contact contact);

	@Delete
	public void deleteContact(Contact contact);
}