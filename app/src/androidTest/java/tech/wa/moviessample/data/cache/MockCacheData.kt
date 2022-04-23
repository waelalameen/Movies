package tech.wa.moviessample.data.cache

import tech.wa.moviessample.data.cache.entities.FavoritesDto
import tech.wa.moviessample.data.cache.entities.Hidden

object MockCacheData {

    val movie_1 = FavoritesDto(
        title = "Bond Girls Are Forever",
        year = "2002",
        id = "tt0353252",
        type = "movie",
        poster = "https://m.media-amazon.com/images/M/MV5BMjMwNTMzNjM5OF5BMl5BanBnXkFtZTgwOTY4OTk1MDE@._V1_SX300.jpg"
    )

    val movie_2 = FavoritesDto(
        title = "Ek Rishtaa: The Bond of Love",
        year = "2001",
        id = "tt0284083",
        type = "movie",
        poster = "https://m.media-amazon.com/images/M/MV5BZmNmOTM5ZGUtNTFiZS00M2E2LTk2NzAtNTJlZmQ0MGQyOTMwXkEyXkFqcGdeQXVyODE5NzE3OTE@._V1_SX300.jpg"
    )

    val movie_3 = FavoritesDto(
        title = "Mad Mission 3: Our Man from Bond Street",
        year = "1984",
        id = "tt0088457",
        type = "movie",
        poster = "https://m.media-amazon.com/images/M/MV5BZWNkNDkzNjUtZWEwMy00YWIyLWFmNzctNjY5N2YyNDA5OGZmXkEyXkFqcGdeQXVyNDkyMzMyODM@._V1_SX300.jpg"
    )

    val hidden_movie_1 = Hidden(
        title = "Bond Girls Are Forever",
        year = "2002",
        id = "tt0353252",
        type = "movie",
        poster = "https://m.media-amazon.com/images/M/MV5BMjMwNTMzNjM5OF5BMl5BanBnXkFtZTgwOTY4OTk1MDE@._V1_SX300.jpg"

    )

    val hidden_movie_2 = Hidden(
        title = "Ek Rishtaa: The Bond of Love",
        year = "2001",
        id = "tt0284083",
        type = "movie",
        poster = "https://m.media-amazon.com/images/M/MV5BZmNmOTM5ZGUtNTFiZS00M2E2LTk2NzAtNTJlZmQ0MGQyOTMwXkEyXkFqcGdeQXVyODE5NzE3OTE@._V1_SX300.jpg"
    )
}