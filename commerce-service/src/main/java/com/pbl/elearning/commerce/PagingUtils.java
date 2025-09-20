package com.pbl.elearning.commerce;

import org.apache.commons.text.CaseUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PagingUtils {

  public static final String ASC = "asc";

  public static final String DESC = "desc";

  private PagingUtils() {}

  /**
   * Handle make page request for query
   *
   * @param sortBy Sort By Field
   * @param order Order By Desc Or Asc
   * @param page Page No
   * @param paging Page Size
   * @return Pageable
   */
  public static Pageable makePageRequest(String sortBy, String order, int page, int paging) {
    String sortField = CaseUtils.toCamelCase(sortBy, false, '_');
    Sort sort =
        ASC.equalsIgnoreCase(order)
            ? Sort.by(sortField).ascending()
            : Sort.by(sortField).descending();
    return PageRequest.of(page - 1, paging, sort);
  }
}