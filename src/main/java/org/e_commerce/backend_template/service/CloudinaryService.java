package org.e_commerce.backend_template.service;

import java.io.IOException;
import java.util.Map;

import org.e_commerce.backend_template.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public Map<String, String> uploadImage(final MultipartFile file, final String folder) {
		try {
			@SuppressWarnings("unchecked")
			final Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
					"folder", folder,
					"resource_type", "image",
					"overwrite", true));

			final String secureUrl = (String) uploadResult.get("secure_url");
			final String publicId = (String) uploadResult.get("public_id");

			log.info("Imagen subida exitosamente a Cloudinary: publicId={}", publicId);
			return Map.of("secure_url", secureUrl, "public_id", publicId);

		} catch (final IOException e) {
			log.error("Error al subir imagen a Cloudinary", e);
			throw new AppException("Error al subir la imagen a Cloudinary: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
		}
	}

	public void deleteImage(final String publicId) {
		try {
			if (publicId != null && !publicId.isBlank()) {
				cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
				log.info("Imagen eliminada de Cloudinary: publicId={}", publicId);
			}
		} catch (final IOException e) {
			log.error("Error al eliminar imagen de Cloudinary: publicId={}", publicId, e);
			throw new AppException("Error al eliminar la imagen de Cloudinary: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
		}
	}
}
