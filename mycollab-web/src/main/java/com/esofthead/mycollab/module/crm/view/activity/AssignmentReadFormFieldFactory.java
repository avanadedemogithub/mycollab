/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.crm.view.activity;

import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.data.CrmLinkBuilder;
import com.esofthead.mycollab.module.crm.domain.SimpleTask;
import com.esofthead.mycollab.module.crm.ui.CrmAssetsManager;
import com.esofthead.mycollab.module.crm.ui.components.RelatedReadItemField;
import com.esofthead.mycollab.vaadin.ui.AbstractBeanFieldGroupViewFieldFactory;
import com.esofthead.mycollab.vaadin.ui.GenericBeanForm;
import com.esofthead.mycollab.vaadin.ui.form.field.DateTimeViewField;
import com.esofthead.mycollab.vaadin.ui.form.field.LinkViewField;
import com.esofthead.mycollab.vaadin.ui.form.field.RichTextViewField;
import com.esofthead.mycollab.vaadin.ui.form.field.UserLinkViewField;
import com.vaadin.ui.Field;

/**
 * 
 * @author MyCollab Ltd.
 * @since 3.0
 * 
 */
class AssignmentReadFormFieldFactory extends
		AbstractBeanFieldGroupViewFieldFactory<SimpleTask> {
	private static final long serialVersionUID = 1L;

	public AssignmentReadFormFieldFactory(GenericBeanForm<SimpleTask> form) {
		super(form);
	}

	@Override
	protected Field<?> onCreateField(Object propertyId) {
		SimpleTask task = attachForm.getBean();

		if (propertyId.equals("assignuser")) {
			return new UserLinkViewField(task.getAssignuser(),
					task.getAssignUserAvatarId(), task.getAssignUserFullName());
		} else if (propertyId.equals("startdate")) {
			return new DateTimeViewField(task.getStartdate());
		} else if (propertyId.equals("duedate")) {
			return new DateTimeViewField(task.getDuedate());
		} else if (propertyId.equals("contactid")) {
			return new LinkViewField(task.getContactName(),
					CrmLinkBuilder.generateContactPreviewLinkFull(task
							.getContactid()),
                    CrmAssetsManager.getAsset(CrmTypeConstants.CONTACT));
		} else if (propertyId.equals("type")) {
			return new RelatedReadItemField(task);

		} else if (propertyId.equals("description")) {
			return new RichTextViewField(task.getDescription());
		}

		return null;
	}

}
