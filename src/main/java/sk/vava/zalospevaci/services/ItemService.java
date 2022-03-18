package sk.vava.zalospevaci.services;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.HibernateUtil;
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

    public List<Item> findByRestaurId(Long restaurId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        return session.createQuery("SELECT i FROM Item i WHERE i.restaurant.id =  :restaurId", Item.class).setParameter("restaurId", restaurId).getResultList();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).get();
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
