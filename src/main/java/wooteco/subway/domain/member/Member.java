package wooteco.subway.domain.member;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Member {
    @Id
    private Long id;
    private String email;
    private String name;
    private String password;
    private Set<Favorite> favorites;

    public Member() {
    }

    public Member(Long id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.favorites = new LinkedHashSet<>();
    }

    public Member(String email, String name, String password) {
        this(null, email, name, password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Set<Favorite> getFavorites() {
        return favorites;
    }

    public void update(String name, String password) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name;
        }
        if (StringUtils.isNotBlank(password)) {
            this.password = password;
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }

    public void deleteFavoriteBy(Long favoriteId) {
        this.favorites.remove(favorites.stream()
                .filter(favorite -> Objects.equals(favorite.getId(), favoriteId))
                .findAny().orElseThrow(IllegalArgumentException::new));
    }
}
