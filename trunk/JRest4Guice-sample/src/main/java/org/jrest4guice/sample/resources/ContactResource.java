package org.jrest4guice.sample.resources;

import org.jrest4guice.client.Page;
import org.jrest4guice.rest.annotations.Delete;
import org.jrest4guice.rest.annotations.Get;
import org.jrest4guice.rest.annotations.MimeType;
import org.jrest4guice.rest.annotations.ModelBean;
import org.jrest4guice.rest.annotations.Parameter;
import org.jrest4guice.rest.annotations.Path;
import org.jrest4guice.rest.annotations.Post;
import org.jrest4guice.rest.annotations.ProduceMime;
import org.jrest4guice.rest.annotations.Put;
import org.jrest4guice.rest.annotations.Remote;
import org.jrest4guice.rest.annotations.ViewTemplate;
import org.jrest4guice.sample.entity.Contact;
import org.jrest4guice.sample.service.ContactService;

import com.google.inject.Inject;

/**
 * 
 * @author <a href="mailto:zhangyouqun@gmail.com">cnoss (QQ：86895156)</a>
 * 
 * 联系人的资源对象，并声明为Remote（可以通过@RemoteService的注入到任一资源对象,通常用在跨应用的资源调用上）
 */
@Path( { "/contact", "/contacts/{contactId}" })
@Remote
public class ContactResource {
	@Inject
	private ContactService service;

	/**
	 * 创建新的联系人
	 * contact 联系人实体
	 */
	@Post
	public String createContact(@ModelBean Contact contact) {
		return this.service.createContact(contact);
	}

	/**
	 * 修改联系人信息
	 * contact 联系人实体
	 */
	@Put
	public void putContact(@ModelBean Contact contact) {
		this.service.updateContact(contact);
	}

	/**
	 * 显示联系人列表，并限定服务所支持的返回数据类型只能为application/json和application/javabean
	 * pageIndex 页码
	 * pageSize 每页记录数
	 */
	@Get
	@ProduceMime( {MimeType.MIME_OF_JSON,MimeType.MIME_OF_JAVABEAN})
	@Path("/contacts")
	public Page<Contact> listContacts(int pageIndex, int pageSize) {
		return this.service.listContacts(pageIndex, pageSize);
	}

	/**
	 * 显示单个联系人的信息，并指定了当请求类型为text/html时的返回视图模块
	 * contactId 联系对象ID
	 */
	@Get
	@ViewTemplate(url="/template/contactDetail.vm")
	public Contact getContact(@Parameter("contactId") String contactId) {
		return this.service.findContactById(contactId);
	}

	/**
	 * 删除指定ID的联系人
	 * contactId 联系对象ID
	 */
	@Delete
	public void deleteContact(@Parameter("contactId") String contactId) {
		this.service.deleteContact(contactId);
	}
}
