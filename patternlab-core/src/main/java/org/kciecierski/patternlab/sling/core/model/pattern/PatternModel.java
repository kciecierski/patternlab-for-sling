package org.kciecierski.patternlab.sling.core.model.pattern;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.kciecierski.patternlab.sling.core.model.breadcrumb.BreadcrumbItemModel;
import org.kciecierski.patternlab.sling.core.utils.PatternLabUtils;

import java.io.IOException;
import java.util.List;

import static org.kciecierski.patternlab.sling.core.utils.PatternLabConstants.DESCRIPTION_EXT;
import static org.kciecierski.patternlab.sling.core.utils.PatternLabConstants.SELECTOR;

public class PatternModel {

    private final String id;

    private final String name;

    private final String path;

    private final String template;

    private final String dataPath;

    private final String code;

    private final String data;

    private final String description;

    private final boolean displayed;

    private final List<BreadcrumbItemModel> breadcrumb;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public String getTemplate() {
        return template;
    }

    public String getPath() {
        return path;
    }

    public String getDataPath() {
        return dataPath;
    }

    public List<BreadcrumbItemModel> getBreadcrumb() {
        return breadcrumb;
    }

    public String getCode() {
        return code;
    }

    public String getData() {
        return data;
    }

    public String getDescription() {
        return description;
    }


    public PatternModel(Resource resource, String appsPath, String patternId, ResourceResolver adminResourceResolver) throws IOException {
        this.id = PatternLabUtils.constructPatternId(resource, appsPath);
        this.name = StringUtils.lowerCase(PatternLabUtils.getResourceTitleOrName(resource));
        this.template = StringUtils.EMPTY;
        this.path = resource.getPath();
        this.dataPath = StringUtils.EMPTY;
        this.displayed = StringUtils.isBlank(patternId) || StringUtils.startsWith(getId(), patternId);
        this.breadcrumb = Lists.newArrayList(new BreadcrumbItemModel(id, name));
        this.code = PatternLabUtils.getDataFromFile(path, adminResourceResolver);
        this.data = null;
        final String descriptionPath = StringUtils.substringBeforeLast(path, SELECTOR) + DESCRIPTION_EXT;
        this.description = PatternLabUtils.getDataFromFile(descriptionPath, adminResourceResolver);
    }

    public PatternModel(Resource resource, String appsPath, String patternId, String jsonDataFile, String templateName, ResourceResolver adminResourceResolver) throws IOException {
        this.id = PatternLabUtils.constructPatternId(resource, appsPath, jsonDataFile, templateName);
        this.name = StringUtils.lowerCase(StringUtils.isBlank(jsonDataFile) ? templateName : jsonDataFile);
        this.template = templateName;
        this.path = resource.getPath();
        this.dataPath = StringUtils.isNotBlank(jsonDataFile) ? resource.getParent().getPath() + "/" + jsonDataFile : StringUtils.EMPTY;
        this.displayed = StringUtils.isBlank(patternId) || StringUtils.startsWith(getId(), patternId);
        this.breadcrumb = Lists.newArrayList(new BreadcrumbItemModel(PatternLabUtils.constructPatternId(resource, appsPath), PatternLabUtils.getResourceTitleOrName(resource)));
        if (StringUtils.isNotBlank(jsonDataFile)) {
            this.breadcrumb.add(new BreadcrumbItemModel(PatternLabUtils.constructPatternId(resource, appsPath, StringUtils.EMPTY, templateName), templateName));
        }
        this.breadcrumb.add(new BreadcrumbItemModel(id, name));
        this.code = PatternLabUtils.getDataFromFile(path, adminResourceResolver);
        this.data = PatternLabUtils.getDataFromFile(dataPath, adminResourceResolver);
        this.description = constructDescription(jsonDataFile, templateName, adminResourceResolver);

    }

    private String constructDescription(String jsonDataFile, String templateName, ResourceResolver adminResourceResolver) throws IOException {
        String description = null;
        if (StringUtils.isNotBlank(jsonDataFile)) {
            final String descriptionPath = StringUtils.substringBeforeLast(dataPath, SELECTOR) + SELECTOR + templateName + DESCRIPTION_EXT;
            description = PatternLabUtils.getDataFromFile(descriptionPath, adminResourceResolver);
        }
        if (StringUtils.isBlank(description)) {
            final String descriptionPath = StringUtils.substringBeforeLast(path, SELECTOR) + SELECTOR + templateName + DESCRIPTION_EXT;
            description = PatternLabUtils.getDataFromFile(descriptionPath, adminResourceResolver);
        }
        if (StringUtils.isBlank(description)) {
            final String descriptionPath = StringUtils.substringBeforeLast(path, SELECTOR) + DESCRIPTION_EXT;
            description = PatternLabUtils.getDataFromFile(descriptionPath, adminResourceResolver);
        }
        return description;
    }

}