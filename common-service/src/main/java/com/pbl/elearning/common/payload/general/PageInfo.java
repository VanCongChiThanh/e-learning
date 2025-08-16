package com.pbl.elearning.common.payload.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
  @JsonProperty("current_page")
  private int currentPage;

  @JsonProperty("next_page")
  private int nextPage;

  @JsonProperty("prev_page")
  private int prevPage;

  @JsonProperty("total_pages")
  private int totalPage;

  @JsonProperty("total_count")
  private long totalCount;

  public PageInfo(int currentPage, int totalPage, long totalCount) {
    this.currentPage = currentPage;
    if (currentPage == 1) {
      this.prevPage = currentPage;
    } else {
      this.prevPage = currentPage - 1;
    }

    if (currentPage == totalPage) {
      this.nextPage = totalPage;
    } else {
      this.nextPage = currentPage + 1;
    }
    this.totalPage = totalPage;
    this.totalCount = totalCount;
  }

  public static PageInfo createPageInfo(Integer currentPage, Integer paging, Long totalCount) {
    int totalPage;
    if (totalCount % paging == 0) {
      totalPage = (int) (totalCount / paging);
    } else {
      totalPage = (int) (totalCount / paging + 1);
    }
    return new PageInfo(currentPage, totalPage, totalCount);
  }
}