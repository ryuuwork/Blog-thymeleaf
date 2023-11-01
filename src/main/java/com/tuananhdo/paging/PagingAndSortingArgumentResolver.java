package com.tuananhdo.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String SORT_FIELD = "sortField";
    public static final String SORT_DIR = "sortDir";
    public static final String REVERSE_SORT_DIR = "reverseSortDir";
    public static final String KEYWORD = "keyword";
    public static final String MODULE_URL = "moduleURL";
    public static final String PAGE_TITLE = "pageTitle";
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PaingAndSortingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer model,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        PaingAndSortingParam anotation = parameter.getParameterAnnotation(PaingAndSortingParam.class);
        String listName = Objects.nonNull(anotation) ? anotation.listName() : null;
        String sortField = webRequest.getParameter(SORT_FIELD);
        String sortDir = webRequest.getParameter(SORT_DIR);
        String keyword = webRequest.getParameter(KEYWORD);
        String reverseSortDir = (Objects.nonNull(sortDir) && sortDir.equals("asc") ? "desc" : "asc");
        if (Objects.nonNull(model)) {
            model.addAttribute(SORT_FIELD, sortField);
            model.addAttribute(SORT_DIR, sortDir);
            model.addAttribute(REVERSE_SORT_DIR, reverseSortDir);
            model.addAttribute(KEYWORD, keyword);
            model.addAttribute(MODULE_URL, (Objects.nonNull(anotation) ? anotation.moduleURL() : null));
            model.addAttribute(PAGE_TITLE, (Objects.nonNull(anotation) ? anotation.pageTitle() : null));
        }
        return new PagingAndSortingHelper(listName, sortField, sortDir, keyword,model);
    }
}
