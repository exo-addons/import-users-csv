/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

@Portlet
@Application(name = "ImportUsersFromCSV")
@Scripts({

        @Script(id = "jquery", value = "js/jquery-1.11.0.min.js"),
        @Script(id = "core", value = "js/jquery.ui.core.min.js", depends = "jquery"),
        @Script(id = "jquery-ui", value = "js/jquery-ui.js", depends = "jquery"),
        @Script(id = "datatables", value = "js/jquery.datatables-1.9.4-custom.js", depends = "jquery"),
/*        @Script(id = "tabletools", value = "js/jquery.datatables.tabletools-2.1.5-min.js"),
        @Script(id = "customizations", value = "js/jquery.datatables.customizations-custom.js", depends = "jquery"),*/
        @Script(id = "csv", value = "js/jquery.csv.js", depends = "jquery"),
        @Script(id = "json", value = "js/jquery.json-2.4.js", depends = "jquery"),
        @Script(id = "importUsersFromCSV", value = "js/importUsersFromCSV.js" , depends = {"jquery", "json", "datatables"})
})
@Stylesheets ({
        @Stylesheet(id = "jquerythemecss", value = "skin/jquery.ui.theme.css"),
        @Stylesheet(id = "dataTablescss", value = "skin/jquery.dataTables.css"),
        @Stylesheet(id = "eXotheme", value = "skin/importUserCSV.css")
})
@Assets("*")

package org.exoplatform.extension.portlet.importUsersFromCSV;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Stylesheets;
import juzu.plugin.asset.Scripts;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;


