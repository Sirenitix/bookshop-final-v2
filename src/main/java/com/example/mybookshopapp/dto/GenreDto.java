package com.example.mybookshopapp.dto;



import com.example.mybookshopapp.entity.Genre;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GenreDto {

    private Genre parentGenre;

    private Genre genre;

    private Integer countBooks;

    private List<Genre> childGenres;


    public GenreDto(Genre genre, List<Genre> childGenres, Genre parentGenre) {
        this.genre = genre;
        this.childGenres = new ArrayList<>(childGenres);
        this.parentGenre = parentGenre;
        this.countBooks = genre.getBooks().size();
    }

    public List<Genre> getChildGenres() {
        return childGenres.stream()
                .sorted((o1, o2) -> o2.getBooks().size() - o1.getBooks().size())
                .collect(Collectors.toList());
    }

    public void setChildGenres(List<Genre> childGenres) {
        this.childGenres = childGenres;
    }
}
