package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ShareitPageRequest pageRequest;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user1@email", "user 1"));
        item1 = itemRepository.save(
                new Item(1L, "Item 1", "Item 1 description", true, user1, null));
        user2 = userRepository.save(new User(2L, "user2@email", "user 2"));
        item2 = itemRepository.save(
                new Item(2L, "Item 2", "Item 2 description", true, user2, null));
        pageRequest = new ShareitPageRequest(0, 10, Sort.unsorted());
    }

    @Test
    void findByOwnerTest() {
        final List<Item> byOwner = itemRepository.findItemsByOwner(user1);
        assertNotNull(byOwner);
        assertEquals(item1.getId(), byOwner.get(0).getId());
        assertEquals(item1.getName(), byOwner.get(0).getName());
        assertEquals(item1.getOwner(), user1);
    }

    @Test
    void findByIdAndOwnerTest() {
        final Item byIdAndOwner = itemRepository.findItemByIdAndOwner(item2.getId(), user2);
        assertNotNull(byIdAndOwner);
        assertEquals(item2.getId(), byIdAndOwner.getId());
        assertEquals(item2.getName(), byIdAndOwner.getName());
        assertEquals(item2.getOwner(), user2);
    }

    @Test
    void searchTest() {
        final List<Item> searched = itemRepository.search("descrip", pageRequest);
        assertNotNull(searched);
        assertEquals(item1.getId(), searched.get(0).getId());
        assertEquals(item1.getName(), searched.get(0).getName());
        assertEquals(item1.getOwner(), searched.get(0).getOwner());
    }
}
