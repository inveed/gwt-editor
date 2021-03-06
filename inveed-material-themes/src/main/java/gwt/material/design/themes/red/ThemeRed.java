/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2017 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package gwt.material.design.themes.red;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.TextResource;
import gwt.material.design.themes.client.ThemeLoader;

public interface ThemeRed extends ThemeLoader.ThemeBundle {
    ThemeRed INSTANCE = GWT.create(ThemeRed.class);

    @Source("gwt/material/design/themes/red/css/materialize.red.min.css")
    TextResource style();

    @Source("gwt/material/design/themes/red/css/overridecss.red.min.css")
    TextResource overrides();
}
