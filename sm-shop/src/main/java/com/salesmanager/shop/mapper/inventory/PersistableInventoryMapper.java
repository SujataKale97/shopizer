package com.salesmanager.shop.mapper.inventory;

import java.util.Date;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.PersistableProductPrice;
import com.salesmanager.shop.model.catalog.product.inventory.PersistableInventory;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.DateUtil;
import io.searchbox.strings.StringUtils;

@Component
public class PersistableInventoryMapper implements Mapper<PersistableInventory, ProductAvailability> {
  
  
  @Autowired
  private LanguageService languageService;

  @Override
  public ProductAvailability convert(PersistableInventory source, MerchantStore store,
      Language language) {
    ProductAvailability availability = new ProductAvailability();
    availability.setMerchantStore(store);
    return convert(source, availability, store, language);
    
  }

  @Override
  public ProductAvailability convert(PersistableInventory source, ProductAvailability destination,
      MerchantStore store, Language language) {
    Validate.notNull(destination, "Product availability cannot be null");
    
    try {

    
    ProductAvailability productAvailability = new ProductAvailability();

    //productAvailability.setProduct(target);
    productAvailability.setProductQuantity(source.getQuantity());
    productAvailability.setProductQuantityOrderMin(source.getProductQuantityOrderMax());
    productAvailability.setProductQuantityOrderMax(source.getProductQuantityOrderMin());
    productAvailability.setAvailable(source.isAvailable());
    productAvailability.setOwner(source.getOwner());
    //productAvailability.setProductStatus();
    productAvailability.setRegion(source.getRegion());
    productAvailability.setRegionVariant(source.getRegionVariant());
    if(!StringUtils.isBlank(source.getDateAvailable())) {
      productAvailability.setProductDateAvailable(DateUtil.getDate(source.getDateAvailable()));
    }
    
    
    for(PersistableProductPrice priceEntity : source.getPrices()) {
      
      ProductPrice price = new ProductPrice();
      price.setId(null);
      if(priceEntity.getId() != null && priceEntity.getId().longValue() > 0) {
        price.setId(priceEntity.getId());
      }
      price.setProductAvailability(productAvailability);
      price.setDefaultPrice(priceEntity.isDefaultPrice());
      price.setProductPriceAmount(priceEntity.getOriginalPrice());
      price.setDefaultPrice(priceEntity.isDefaultPrice());
      price.setCode(priceEntity.getCode());
      price.setProductPriceSpecialAmount(priceEntity.getDiscountedPrice());
      if(priceEntity.getDiscountStartDate()!=null) {
          Date startDate = DateUtil.getDate(priceEntity.getDiscountStartDate());
          price.setProductPriceSpecialStartDate(startDate);
      }
      if(priceEntity.getDiscountEndDate()!=null) {
          Date endDate = DateUtil.getDate(priceEntity.getDiscountEndDate());
          price.setProductPriceSpecialEndDate(endDate);
      }
      productAvailability.getPrices().add(price);
      price.setProductAvailability(productAvailability);
      
      java.util.List<com.salesmanager.shop.model.catalog.product.ProductPriceDescription> descriptions = priceEntity.getDescriptions();
      if(descriptions != null) {
        for(com.salesmanager.shop.model.catalog.product.ProductPriceDescription desc : descriptions) {
          ProductPriceDescription description = getDescription(desc);
          description.setProductPrice(price);
          price.getDescriptions().add(description);
        }
      }
    }
    
    return productAvailability;
    
    } catch(Exception e) {
      throw new ConversionRuntimeException(e);
    }

  }
  
  private ProductPriceDescription getDescription(com.salesmanager.shop.model.catalog.product.ProductPriceDescription desc) throws ConversionException {
    ProductPriceDescription target = new ProductPriceDescription();
    target.setDescription(desc.getDescription());
    target.setName(desc.getName());
    target.setTitle(desc.getTitle());
    target.setId(null);
    if(desc.getId()!=null && desc.getId().longValue()>0) {
      target.setId(desc.getId());
    }

    Language lang;
    try {
      lang = languageService.getByCode(desc.getLanguage());
      target.setLanguage(lang);
      if(lang==null) {
        throw new ConversionException("Language is null for code " + desc.getLanguage() + " use language ISO code [en, fr ...]");
    }
    } catch (ServiceException e) {
      throw new ConversionException(e);
    }

    return target;

  }

}
