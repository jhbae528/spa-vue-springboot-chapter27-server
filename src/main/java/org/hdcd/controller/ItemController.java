package org.hdcd.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.hdcd.domain.Item;
import org.hdcd.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/items")
public class ItemController {
	
	private final ItemService itemService;
	
	@Value("${upload.path}")
	private String uploadPath;
	
	@GetMapping
	public ResponseEntity<List<Item>> list() throws Exception {
		log.info("list");
		List<Item> itemList = this.itemService.list();
		
		return new ResponseEntity<List<Item>>(itemList, HttpStatus.OK);				
	}
	
	@PostMapping
	public ResponseEntity<Item> register(@RequestPart("item") String itemString, @RequestPart("file") MultipartFile picture) throws Exception {
		log.info("itemString: " + itemString);
		
		Item item = new ObjectMapper().readValue(itemString, Item.class);
		
		String itemName = item.getItemName();
		String description = item.getDescription();
		
		log.info("itemName = " + itemName);
		log.info("description = " + description);
		
		if (itemName != null) {
			item.setItemName(itemName);
		}
		
		if (description != null) {
			item.setDescription(description);
		}
		
		item.setPicture(picture);
		
		MultipartFile file = item.getPicture();
		
		log.info("originalFileName = " + file.getOriginalFilename());
		log.info("size = " + file.getSize());
		log.info("contentType = " + file.getContentType());
		
		String createdFileName = uploadFile(file.getOriginalFilename(), file.getBytes());
		
		item.setPictureUrl(createdFileName);
		
		this.itemService.regist(item);

		log.info("itemId = " + item.getItemId());
		
		Item createdItem = new Item();
		createdItem.setItemId(item.getItemId());
		
		return new ResponseEntity<Item>(createdItem, HttpStatus.OK);
	}

	@GetMapping("/display")	
	public ResponseEntity<byte[]> displayFile(Long itemId) throws Exception {
		
		ResponseEntity<byte[]> entity = null;
		
		String fileName = itemService.getPicture(itemId);
		
		log.info("FILE NAME: " + fileName);
		
		try {
			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
			
			MediaType mediaType = getMediaType(formatName);
			
			HttpHeaders headers = new HttpHeaders();
			
			File file = new File(uploadPath + File.separator + fileName);
			
			if (mediaType != null) {
				headers.setContentType(mediaType);
			}
			
			entity = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.CREATED);
		}
		catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}
	
	@GetMapping("/{itemId}")
	public ResponseEntity<Item> read(@PathVariable("itemId") Long itemId) throws Exception {
		log.info("read");
		
		Item item = this.itemService.read(itemId);
		
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}
	
	@DeleteMapping("/{itemId}")
	public ResponseEntity<Void> remove(@PathVariable("itemId") Long itemId) throws Exception {
		
		this.itemService.remove(itemId);
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping
	public ResponseEntity<Item> modify(@RequestPart("item") String itemString,
			@RequestPart(name="file", required = false) MultipartFile picture) throws Exception {
		
		log.info("itemString: " + itemString);
		
		Item item = new ObjectMapper().readValue(itemString, Item.class);
		
		String itemName = item.getItemName();
		String description = item.getDescription();
		
		log.info("itemName: " + itemName);
		log.info("description: " + description);
	
		if (itemName != null) {
			item.setItemName(itemName);
		}
		
		if (description != null) {
			item.setDescription(description);
		}
		
		if(picture != null) {
			item.setPicture(picture);
			
			MultipartFile file = item.getPicture();
			
			log.info("originalName: " + file.getOriginalFilename());
			log.info("size: " + file.getSize());
			log.info("contentType: " + file.getContentType());
			
			String createdFileName = uploadFile(file.getOriginalFilename(), file.getBytes());
			
			item.setPictureUrl(createdFileName);
		}
		else {
			Item oldItem = this.itemService.read(item.getItemId());
			item.setPictureUrl(oldItem.getPictureUrl());
		}
		
		this.itemService.modify(item);
		
		Item modifiedItem = new Item();
		modifiedItem.setItemId(item.getItemId());				
		
		return new ResponseEntity<Item>(modifiedItem, HttpStatus.OK);
	}
	
	private String uploadFile(String originName, byte[] data) throws Exception {
		UUID uid = UUID.randomUUID();
		
		String createdFileName = uid.toString() + "_" + originName;
		
		File target = new File(uploadPath);
		
		FileCopyUtils.copy(data, target);
				
		return createdFileName;
	}
	
	private MediaType getMediaType(String formatName) {
		MediaType mediaType = null;
		
		if (formatName != null) {
			if (formatName.equals("JPG")) {
				mediaType = MediaType.IMAGE_JPEG;
			}
			else if (formatName.equals("GIF")) {
				mediaType = MediaType.IMAGE_GIF;
			}
			else if (formatName.equals("PNG")) {
				mediaType = MediaType.IMAGE_PNG;
			}
		}
		return mediaType;
	}

}
