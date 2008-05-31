package org.jrest4guice;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrest4guice.annotations.HttpMethodType;
import org.jrest4guice.annotations.Remote;
import org.jrest4guice.context.HttpContextManager;
import org.jrest4guice.context.JRestContext;
import org.jrest4guice.context.ModelMap;
import org.jrest4guice.core.guice.GuiceContext;
import org.jrest4guice.core.util.ClassUtils;
import org.jrest4guice.exception.RestMethodNotFoundException;
import org.jrest4guice.writer.JsonResponseWriter;

/**
 * 
 * @author <a href="mailto:zhangyouqun@gmail.com">cnoss</a>
 * 
 */
@SuppressWarnings("unchecked")
public class RequestProcessor {

	public static final String METHOD_OF_GET = "get";
	public static final String METHOD_OF_POST = "post";
	public static final String METHOD_OF_PUT = "put";
	public static final String METHOD_OF_DELETE = "delete";

	private String charset;

	private String urlPrefix;

	public RequestProcessor setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
		return this;
	}

	/**
	 * 处理来自客户端的请求
	 * 
	 * @param servletReqest
	 * @param servletResponse
	 */
	public void process(ServletRequest servletReqest,
			ServletResponse servletResponse) throws Throwable {
		HttpServletRequest request = (HttpServletRequest) servletReqest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		// 获取字符编码
		charset = request.getCharacterEncoding();
		if (charset == null || charset.trim().equals("")) {
			charset = "UTF-8";
			try {
				request.setCharacterEncoding(charset);
			} catch (Exception e) {
			}
		}

		String uri = request.getRequestURI();
		String uri_bak = uri;
		String contextPath = request.getContextPath();
		if (!contextPath.trim().equals("/"))
			uri = uri.replace(contextPath, "");

		if (this.urlPrefix != null)
			uri = uri.replace(this.urlPrefix, "");

		// REST资源的参数，这些参数都包含在URL中
		ModelMap<String, String> params = new ModelMap<String, String>();
		// 设置上下文中的环境变量
		HttpContextManager.setContext(request, response, params);
		try {
			int index;
			if ((index = uri.indexOf(Remote.REMOTE_SERVICE_PREFIX)) != -1) {
				String serviceName = request
						.getParameter(Remote.REMOTE_SERVICE_NAME_KEY);
				String methodIndex = request
						.getParameter(Remote.REMOTE_SERVICE_METHOD_INDEX_KEY);
				Class<?> clazz = JRestContext.getInstance().getRemoteService(
						serviceName);
				if (clazz != null) {
					index = Integer.parseInt(methodIndex);
					ServiceExecutor exec = GuiceContext.getInstance().getBean(
							ServiceExecutor.class);
					List<Method> methods = ClassUtils.getSortedMethodList(clazz);
					Service service = new Service(GuiceContext.getInstance()
							.getBean(clazz), methods.get(index));
					// 填充参数
					fillParameters(request, params);
					exec.execute(service, this.getHttpMethodType(RequestProcessor.METHOD_OF_POST),
							charset);
				} else {
					this.writeRestServiceNotFoundMessage(uri_bak);
				}
			} else {
				// 从REST资源注册表中查找此URI对应的资源
				Service service = JRestContext.getInstance()
						.lookupResource(uri);
				if (service != null) {
					ServiceExecutor exec = GuiceContext.getInstance().getBean(
							ServiceExecutor.class);
					// 填充参数
					fillParameters(request, params);
					// 根据不同的请求方法调用REST对象的不同方法
					String method = request.getMethod();
					exec.execute(service, this.getHttpMethodType(method),
							charset);
				} else {
					this.writeRestServiceNotFoundMessage(uri_bak);
				}
			}
		} catch (RestMethodNotFoundException e) {
			this.writeRestServiceNotFoundMessage(uri_bak);
		} finally {
			// 清除上下文中的环境变量
			HttpContextManager.clearContext();
		}
	}

	private void writeRestServiceNotFoundMessage(String uri) {
		GuiceContext.getInstance().getBean(JsonResponseWriter.class)
				.writeResult(new Exception("没有提供指定的Rest服务 (" + uri + ") ！"),
						charset);
	}

	private HttpMethodType getHttpMethodType(String method) {
		if (METHOD_OF_GET.equalsIgnoreCase(method))
			return HttpMethodType.GET;
		else if (METHOD_OF_POST.equalsIgnoreCase(method))
			return HttpMethodType.POST;
		else if (METHOD_OF_PUT.equalsIgnoreCase(method))
			return HttpMethodType.PUT;
		else if (METHOD_OF_DELETE.equalsIgnoreCase(method))
			return HttpMethodType.DELETE;
		return null;
	}

	/**
	 * 填充参数
	 * 
	 * @modelMap request
	 * @modelMap params
	 */
	private void fillParameters(HttpServletRequest request, ModelMap params) {
		Enumeration names = request.getAttributeNames();
		String name;
		while (names.hasMoreElements()) {
			name = names.nextElement().toString();
			params.put(name, request.getAttribute(name));
		}

		// url中的参数
		names = request.getParameterNames();
		while (names.hasMoreElements()) {
			name = names.nextElement().toString();
			params.put(name, request.getParameter(name));
		}

		// 以http body方式提交的参数
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					request.getInputStream(), charset));
			// Read the request
			CharArrayWriter data = new CharArrayWriter();
			char buf[] = new char[4096];
			int ret;
			while ((ret = in.read(buf, 0, 4096)) != -1)
				data.write(buf, 0, ret);

			// URL解码
			String content = URLDecoder.decode(data.toString().trim(), charset);
			// 组装参数
			if (content != "") {
				String[] param_pairs = content.split("&");
				String[] kv;
				for (String p : param_pairs) {
					kv = p.split("=");
					if (kv.length > 1)
						params.put(kv[0], kv[1]);
				}
			}

			data.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}