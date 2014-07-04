package com.esofthead.mycollab.module.mail;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.esofthead.mycollab.configuration.SharingOptions;
import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.core.MyCollabException;
import com.esofthead.mycollab.template.velocity.TemplateContext;

/**
 * 
 * @author MyCollab Ltd.
 * @since 4.3.0
 * 
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContentGenerator implements IContentGenerator, InitializingBean {

	private TemplateContext templateContext;

	@Autowired
	private VelocityEngine templateEngine;

	@Override
	public void putVariable(String key, Object value) {
		templateContext.put(key, value);
	}

	@Override
	public String generateSubjectContent(String subject) {
		StringWriter writer = new StringWriter();
		Reader reader = new StringReader(subject);
		templateEngine.evaluate(templateContext.getVelocityContext(), writer,
				"log task", reader);
		return writer.toString();
	}

	@Override
	public String generateBodyContent(String templateFilePath) {
		StringWriter writer = new StringWriter();
		Reader reader;
		try {
			reader = new InputStreamReader(TemplateGenerator.class
					.getClassLoader().getResourceAsStream(templateFilePath),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			reader = new InputStreamReader(TemplateGenerator.class
					.getClassLoader().getResourceAsStream(templateFilePath));
		} catch (Exception e) {
			throw new MyCollabException("Exception while read file path "
					+ templateFilePath, e);
		}

		templateEngine.evaluate(templateContext.getVelocityContext(), writer,
				"log task", reader);
		return writer.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		templateContext = new TemplateContext();
		Map<String, String> defaultUrls = new HashMap<String, String>();

		SharingOptions sharingOptions = SharingOptions
				.getDefaultSharingOptions();

		defaultUrls.put("cdn_url", SiteConfiguration.getCdnUrl());
		defaultUrls.put("facebook_url", sharingOptions.getFacebookUrl());
		defaultUrls.put("google_url", sharingOptions.getGoogleplusUrl());
		defaultUrls.put("linkedin_url", sharingOptions.getLinkedinUrl());
		defaultUrls.put("twitter_url", sharingOptions.getTwitterUrl());

		templateContext.put("defaultUrls", defaultUrls);
	}

}