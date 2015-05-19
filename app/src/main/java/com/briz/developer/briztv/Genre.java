package com.briz.developer.briztv;

/**
 * Класс жанров ТВ
 * @version 18.05.15.
 */
public class Genre {

    public String genre_id;
    public String genre_desc;

    public void InitGenres() {

        genre_id = "all";
        genre_desc = "все жанры";

    }

    public void InitGenres(Genre genre) {

        genre_id = genre.genre_id;
        genre_desc = genre.genre_desc;

    }

}
