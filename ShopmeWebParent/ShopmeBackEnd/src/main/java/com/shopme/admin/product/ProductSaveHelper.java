package com.shopme.admin.product;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.AmazonS3Util;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.product.ProductImage;

public class ProductSaveHelper {

	static void deleteExtraImagesWeredRemovedOnForm(Product product) {
		String extraImageDir = "product-images/" + product.getId() + "/extras";
		List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDir);

		for (String objectKey : listObjectKeys) {
			int lastIndexOfSlash = objectKey.lastIndexOf("/");
			String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());

			if (!product.containsImageName(fileName)) {
				AmazonS3Util.deleteFile(objectKey);
				System.out.println("Deleted extra image: " + objectKey);
			}
		}
	}

	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		if (imageIDs == null || imageIDs.length == 0)
			return;

		Set<ProductImage> images = new HashSet<>();

		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];

			images.add(new ProductImage(id, name, product));
		}

		product.setImages(images);

	}

	static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		if (detailNames == null || detailNames.length == 0)
			return;

		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);

			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
	}

	static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Product savedProduct) throws IOException {

		/*
		 * String main_img_name = mainImageMultipart.getOriginalFilename(); String path
		 * = "../ShopmeFrontEnd/src/main/resources/static/images/"; byte[] com_image =
		 * main_img_name.getBytes();
		 * 
		 * Path company_i_path = Paths.get(path + main_img_name);
		 * Files.write(company_i_path, com_image);
		 * 
		 * 
		 * for(MultipartFile name:extraImageMultiparts) {
		 * 
		 * String extra_img_name = name.getOriginalFilename(); byte[] com_extra_image =
		 * extra_img_name.getBytes();
		 * 
		 * Path company_i_epath = Paths.get(path + extra_img_name);
		 * Files.write(company_i_epath, com_extra_image);
		 * 
		 * }
		 */

		FileOutputStream fout = new FileOutputStream(
				"../ShopmeFrontEnd/src/main/resources/static/images/" + mainImageMultipart.getOriginalFilename());
		fout.write(mainImageMultipart.getBytes());

		for (MultipartFile name : extraImageMultiparts) {
			FileOutputStream fout1 = new FileOutputStream(
					"../ShopmeFrontEnd/src/main/resources/static/images/" + name.getOriginalFilename());
			fout1.write(name.getBytes());

		}
	}
}
