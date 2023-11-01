package com.tuananhdo.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public class PagingAndSortingHelper {

    public static final String CURRENT_PAGE = "currentPage";
    public static final String START_COUNT = "startCount";
    public static final String END_COUNT = "endCount";
    public static final String TOTAL_ITEMS = "totalItems";
    public static final String TOTAL_PAGES = "totalPages";

    private final String listName;
    private final String sortField;
    private final String sortDir;
    private final String keyword;
    private final ModelAndViewContainer model;

    public void updateModelAttributes(int pageNumber, Page<?> page) {
        List<?> pageAllContent = page.getContent();
        int pageSize = page.getSize();

        int startCount = (pageNumber - 1) * pageSize + 1;
        long endCount = Math.min(startCount + pageSize - 1, page.getTotalElements());

        model.addAttribute(listName, pageAllContent);
        model.addAttribute(START_COUNT, startCount);
        model.addAttribute(END_COUNT, endCount);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(TOTAL_ITEMS, page.getTotalElements());
        model.addAttribute(TOTAL_PAGES, page.getTotalPages());
    }

    public <T, U> Page<U> sortAndPagingOrSearchAllPage(int pageNumber, int pageSize,
                                                       SearchRepository<T, Long> searchRepository,
                                                       Function<? super T, ? extends U> mapToFunction) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<T> page = findAllPage(searchRepository, pageable);
        return page.map(mapToFunction);
    }


    private <T> Page<T> findAllPage(SearchRepository<T, Long> searchRepository,
                                    Pageable pageable) {
        return Objects.nonNull(keyword) ?
                searchRepository.findAllByKeyWord(pageable, keyword) :
                searchRepository.findAll(pageable);
    }

    public Pageable createPageable(int pageNumber, int pageSize) {
        Sort sort;
        if (Objects.nonNull(sortDir) && Objects.nonNull(sortField)) {
            sort = sortDir.equals(Sort.Direction.ASC.name()) ?
                    Sort.by(Sort.Order.asc(sortField)) :
                    Sort.by(Sort.Order.desc(sortField));
        } else {
            sort = Sort.unsorted();
        }
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }
}
