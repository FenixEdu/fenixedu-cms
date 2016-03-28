/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.exceptions.CmsDomainException;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

@DefaultJsonAdapter(PostFile.class)
public class PostFileAdapter implements JsonAdapter<PostFile> {

    @Override
    public PostFile create(JsonElement json, JsonBuilder ctx) {
        // Depends on Post, implemented in {@PostResource.createFileFromRequest}
        return null;
    }

    @Override
    public PostFile update(JsonElement json, PostFile postFile, JsonBuilder ctx) {
        if(postFile.getPost()!=null && postFile.getPost().isStaticPost()) {
            ensureCanDoThis(postFile.getSite(), SEE_PAGES, EDIT_PAGE);
        } else {
            ensureCanDoThis(postFile.getSite(), EDIT_POSTS);
        }
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("index") && !jObj.get("index").isJsonNull()) {
            postFile.setIndex(jObj.get("index").getAsInt());
        }

        if (jObj.has("isEmbedded") && !jObj.get("isEmbedded").isJsonNull()) {
            postFile.setIsEmbedded(jObj.get("isEmbedded").getAsBoolean());
        }

        if (jObj.has("accessGroup") && !jObj.get("accessGroup").isJsonNull()) {
            postFile.getFiles().setAccessGroup(Group.parse(jObj.get("accessGroup").getAsString()));
        }

        Signal.emit(PostFile.SIGNAL_EDITED, new DomainObjectEvent<>(postFile));
        return postFile;
    }

    @Override
    public JsonElement view(PostFile postFile, JsonBuilder ctx) {
        if(postFile.getPost()!=null && postFile.getPost().isStaticPost()) {
            ensureCanDoThis(postFile.getSite(), SEE_PAGES);
        }

        JsonObject json = new JsonObject();

        json.addProperty("id", postFile.getExternalId());
        json.addProperty("index", postFile.getIndex());
        json.addProperty("isEmbedded", postFile.getIsEmbedded());
        json.addProperty("post", postFile.getPost().getExternalId());

        GroupBasedFile file = postFile.getFiles();
        json.addProperty("checksum", file.getChecksum());
        json.addProperty("contentType", file.getContentType());
        json.addProperty("displayName", file.getDisplayName());
        json.addProperty("filename", file.getFilename());
        json.addProperty("size", file.getSize());

        if (file.getAccessGroup() != null) {
            if(!file.getAccessGroup().isMember(Authenticate.getUser())) {
                throw CmsDomainException.forbiden();
            }
            json.addProperty("accessGroup", file.getAccessGroup().getPresentationName());
        }

        json.addProperty("url", FileDownloadServlet.getDownloadUrl(file));

        return json;
    }

}
