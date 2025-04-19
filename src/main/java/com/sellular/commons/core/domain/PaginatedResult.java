package com.sellular.commons.core.domain;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaginatedResult<T> {

    private List<T> result;

    private boolean nextPage;

    private long totalCount;

}
