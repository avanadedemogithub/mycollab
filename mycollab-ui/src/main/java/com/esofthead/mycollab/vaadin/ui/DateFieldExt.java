/**
 * This file is part of mycollab-ui.
 *
 * mycollab-ui is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-ui is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-ui.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.vaadin.ui;

import com.esofthead.mycollab.vaadin.AppContext;
import com.vaadin.ui.DateField;

import java.util.Date;

/**
 *
 * @author MyCollab Ltd.
 * @since 4.5.4
 *
 */
public class DateFieldExt extends DateField {
    private static final long serialVersionUID = 1L;

    public DateFieldExt() {
        this(null);
    }

    public DateFieldExt(String caption) {
        this(caption, null);
    }

    public DateFieldExt(String caption, Date value) {
        super(caption, value);
        this.setDateFormat(AppContext.getUserDateFormat().getDateFormat());
        this.setTimeZone(AppContext.getTimezone());
    }
}
