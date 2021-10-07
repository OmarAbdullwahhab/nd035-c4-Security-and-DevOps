package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp()
    {
        System.out.println("Running Setup");
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository", itemRepository);


        when(itemRepository.findById(1l)).thenReturn(
                Optional.of(samples().get(0))
        );

        when(itemRepository.findById(2l)).thenReturn(
                Optional.of(samples().get(1))
        );

        when(itemRepository.findByName("Item 1")).thenReturn(
                Arrays.asList(samples().get(0))
        );

        when(itemRepository.findByName("Item 2")).thenReturn(
                Arrays.asList(samples().get(1))
        );
        when(itemRepository.findAll()).thenReturn(
                samples()
        );
    }
    private List<Item> samples()
    {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(1245.45));
        item.setName("Item 1");
        item.setId(1l);
        item.setDescription("Item 1 desc");
        items.add(0,item);

        item = new Item();
        item.setPrice(BigDecimal.valueOf(1478.75));
        item.setName("Item 2");
        item.setId(2l);
        item.setDescription("Item 2 desc");
        items.add(1,item);

        return items;
    }

    @Test
    public void testGetItemById()
    {
        ResponseEntity<Item> found = this.itemController.getItemById(2l);
        assertEquals(200, found.getStatusCodeValue());
        assertEquals(Long.valueOf(2),found.getBody().getId());

    }
    @Test
    public void testGetItems()
    {
        ResponseEntity<List<Item>> found = this.itemController.getItems();
        assertEquals(200, found.getStatusCodeValue());
        assertEquals(Long.valueOf(1l),found.getBody().get(0).getId());

    }
    @Test
    public void testGetItemsByName()
    {
        ResponseEntity<List<Item>> found = this.itemController.getItemsByName("Item 1");
        assertEquals(200, found.getStatusCodeValue());
        assertTrue(found.getBody().size()>0);
        assertFalse(found.getBody().size() > 1);

    }

}
