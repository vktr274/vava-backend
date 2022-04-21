package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Item;
import sk.vava.zalospevaci.repositories.ItemRepository;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getByRestaurantId(Long restaurantId) throws NotFoundException {
        var items = itemRepository.findAllByRestaurantId(restaurantId).orElse(null);
        if (items == null) {
            throw new NotFoundException("no items for " + restaurantId.toString());
        }
        return items;
    }

    public Item getItemById(Long id) throws NotFoundException {
        var item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return item;
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Item item) {
        itemRepository.delete(item);
    }

    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }
}
