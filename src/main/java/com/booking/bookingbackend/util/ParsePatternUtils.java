package com.booking.bookingbackend.util;

import static com.booking.bookingbackend.constant.CommonConstant.SEARCH_OPERATOR;
import static com.booking.bookingbackend.constant.CommonConstant.SORT_BY;

import com.booking.bookingbackend.data.repository.criteria.SearchOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class ParsePatternUtils {

  public static String parseSortPattern(String... sort) {
    // sort: rating:asc
    StringBuilder orderBy = new StringBuilder();
    if (sort != null) {
      for (String s : sort) {
        if (StringUtils.hasLength(s)) {
          Pattern pattern = Pattern.compile(SORT_BY);
          Matcher matcher = pattern.matcher(s);

          if (matcher.find()) {
            orderBy.append(matcher.group(1))
                .append(" ")
                .append(matcher.group(3).toUpperCase());
            if (!s.equals(sort[sort.length - 1])) {
              orderBy.append(", ");
            }
          } else {
            log.warn("Invalid sort parameter: {}", s);
          }
        }
      }
    }
    return orderBy.toString();
  }

  public static List<SearchOperation> parseFilterPattern(String[] filters) {
    List<SearchOperation> searchOperations = new ArrayList<>();
    if (filters != null) {
      for (String filter : filters) {
        if (StringUtils.hasLength(filter)) {
          Pattern pattern = Pattern.compile(SEARCH_OPERATOR);
          Matcher matcher = pattern.matcher(filter);

          if (matcher.find()) {
            SearchOperation searchOperation = new SearchOperation();
            searchOperation.setKey(matcher.group(1));
            searchOperation.setOperator(matcher.group(2).equals(":") ? "=" : matcher.group(2));
            searchOperation.setValue(matcher.group(3));
            searchOperations.add(searchOperation);
          }
        }
      }
    }
    return searchOperations;
  }
}
