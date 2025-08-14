package com.book.store.DTO;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class BookDTO {
    private Long id;
    @NonNull
    private String author;
    @NonNull
    private String title;
    private String description;
    @NonNull
    private Integer quantity;
    @NonNull
    private Float price;
}
