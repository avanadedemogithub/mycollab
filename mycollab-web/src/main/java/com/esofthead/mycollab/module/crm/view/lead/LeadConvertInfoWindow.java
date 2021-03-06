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
package com.esofthead.mycollab.module.crm.view.lead;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.crm.domain.Opportunity;
import com.esofthead.mycollab.module.crm.domain.SimpleLead;
import com.esofthead.mycollab.module.crm.events.LeadEvent;
import com.esofthead.mycollab.module.crm.i18n.LeadI18nEnum;
import com.esofthead.mycollab.module.crm.service.LeadService;
import com.esofthead.mycollab.module.crm.view.campaign.CampaignSelectionField;
import com.esofthead.mycollab.module.crm.view.opportunity.OpportunitySalesStageComboBox;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 * 
 * @author MyCollab Ltd.
 * @since 3.0
 * 
 */
public class LeadConvertInfoWindow extends Window {

	private static final long serialVersionUID = -4005327071240226216L;

	private final SimpleLead lead;
	private LeadOpportunityForm opportunityForm;

	public LeadConvertInfoWindow(SimpleLead lead) {
		super();

		this.lead = lead;
		this.setWidth("900px");
		this.setContent(initContent());
		this.setResizable(false);
		this.setModal(true);
		this.center();

        setCaption(AppContext.getMessage(
                LeadI18nEnum.WINDOW_CONVERT_LEAD_TITLE, lead.getLastname(),
                lead.getFirstname()));
	}

	public Layout initContent() {
		CssLayout contentLayout = new CssLayout();
		contentLayout.setWidth("100%");
		contentLayout.setStyleName("lead-convert-window");

		contentLayout.addComponent(createBody());
		ComponentContainer buttonControls = createButtonControls();
		if (buttonControls != null) {
			final HorizontalLayout controlPanel = new HorizontalLayout();
			buttonControls.setSizeUndefined();
			controlPanel.addComponent(buttonControls);
			controlPanel.setWidth("100%");
			controlPanel.setMargin(true);
			controlPanel.setComponentAlignment(buttonControls,
					Alignment.MIDDLE_CENTER);
			contentLayout.addComponent(controlPanel);
		}

		return contentLayout;
	}

	private ComponentContainer createButtonControls() {
		final MHorizontalLayout layout = new MHorizontalLayout().withStyleName("addNewControl");

		Button convertButton = new Button(
				AppContext.getMessage(LeadI18nEnum.BUTTON_CONVERT_LEAD),
				new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						LeadService leadService = ApplicationContextUtil
								.getSpringBean(LeadService.class);
						lead.setStatus("Converted");
						leadService.updateWithSession(lead,
								AppContext.getUsername());
						Opportunity opportunity = null;
						if (opportunityForm != null
								&& opportunityForm.isVisible()) {
							if (opportunityForm.validateForm()) {
								opportunity = opportunityForm.getBean();
							}
						}

						leadService.convertLead(lead, opportunity,
								AppContext.getUsername());
						LeadConvertInfoWindow.this.close();
						EventBusFactory.getInstance().post(
								new LeadEvent.GotoRead(
										LeadConvertInfoWindow.this, lead
												.getId()));
					}
				});
		convertButton.setStyleName(UIConstants.THEME_GREEN_LINK);
		layout.addComponent(convertButton);
		layout.setComponentAlignment(convertButton, Alignment.MIDDLE_CENTER);

		Button cancelButton = new Button(
				AppContext.getMessage(GenericI18Enum.BUTTON_CANCEL),
				new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						LeadConvertInfoWindow.this.close();

					}
				});
		cancelButton.setStyleName(UIConstants.THEME_GRAY_LINK);
		layout.addComponent(cancelButton);
		layout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		return layout;
	}

	private ComponentContainer createBody() {
		final CssLayout layout = new CssLayout();
		layout.setSizeFull();

		Label shortDescription = new Label(
				"<p>&nbsp;&nbsp;&nbsp;By clicking the \"Convert\" button, the following tasks will be done:</p>",
				ContentMode.HTML);
		layout.addComponent(shortDescription);

		MVerticalLayout infoLayout = new MVerticalLayout().withMargin(new MarginInfo(false, true, true, true));

		String createAccountTxt = "Create Account: <span class='"
				+ UIConstants.TEXT_BLUE + "'>" + lead.getAccountname()
				+ "</span>";
		Label createAccountLbl = new Label(createAccountTxt, ContentMode.HTML);
		createAccountLbl.addStyleName(UIConstants.LABEL_CHECKED);
		infoLayout.addComponent(createAccountLbl);

		String createContactTxt = "Create Contact: <span class='"
				+ UIConstants.TEXT_BLUE
				+ "'>"
				+ lead.getLastname()
				+ (lead.getFirstname() != null ? " " + lead.getFirstname() : "")
				+ "</span>";
		Label createContactLbl = new Label(createContactTxt, ContentMode.HTML);
		createContactLbl.addStyleName(UIConstants.LABEL_CHECKED);
		infoLayout.addComponent(createContactLbl);

		final CheckBox isCreateOpportunityChk = new CheckBox(
				"Create a new opportunity for this account");
		isCreateOpportunityChk
				.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						Boolean isSelected = isCreateOpportunityChk.getValue();
						if (isSelected) {
							opportunityForm = new LeadOpportunityForm();
							Opportunity opportunity = new Opportunity();

							// this is a trick to pass validation
							opportunity.setAccountid(0);
							opportunityForm.setBean(opportunity);
							layout.addComponent(opportunityForm);
						} else {
							layout.removeComponent(opportunityForm);
						}

					}
				});

		infoLayout.addComponent(isCreateOpportunityChk);

		layout.addComponent(infoLayout);
		return layout;
	}

	private class LeadOpportunityForm extends AdvancedEditBeanForm<Opportunity> {
		private static final long serialVersionUID = 1L;

		public LeadOpportunityForm() {
			super();
			this.setFormLayoutFactory(new IFormLayoutFactory() {
				private static final long serialVersionUID = 1L;

				private GridFormLayoutHelper informationLayout;

				@Override
				public ComponentContainer getLayout() {
					this.informationLayout =  GridFormLayoutHelper.defaultFormLayoutHelper(2, 3);
					return informationLayout.getLayout();
				}

				@Override
				public void attachField(Object propertyId, Field<?> field) {
					if (propertyId.equals("opportunityname")) {
						informationLayout.addComponent(field, "Opportunity", 0,
								0);
					} else if (propertyId.equals("expectedcloseddate")) {
						informationLayout.addComponent(field,
								"Expected Close Date", 1, 0);
					} else if (propertyId.equals("salesstage")) {
						informationLayout.addComponent(field, "Sales Stage", 0,
								1);
					} else if (propertyId.equals("campaignid")) {
						informationLayout.addComponent(field, "Campaign", 1, 1);
					} else if (propertyId.equals("amount")) {
						informationLayout.addComponent(field, "Amount", 0, 2);
					} else if (propertyId.equals("currencyid")) {
						informationLayout.addComponent(field, "Currency", 1, 2);
					}
				}
			});

			this.setBeanFormFieldFactory(new AbstractBeanFieldGroupEditFieldFactory<Opportunity>(
					this) {
				private static final long serialVersionUID = 1L;

				@Override
				protected Field<?> onCreateField(Object propertyId) {
					if (propertyId.equals("campaignid")) {
						return new CampaignSelectionField();
					} else if (propertyId.equals("opportunityname")) {
						TextField tf = new TextField();
						if (isValidateForm) {
							tf.setNullRepresentation("");
							tf.setRequired(true);
							tf.setRequiredError("Name must not be null");
						}
						return tf;
					} else if (propertyId.equals("currencyid")) {
						CurrencyComboBoxField currencyBox = new CurrencyComboBoxField();
						return currencyBox;
					} else if (propertyId.equals("salesstage")) {
						return new OpportunitySalesStageComboBox();
					} else if (propertyId.equals("accountId")) {
						return new DummyCustomField();
					}

					return null;
				}
			});
		}
	}
}