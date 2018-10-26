package net.inveed.gwt.editor.client.model;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import com.github.nmorel.gwtjackson.client.utils.Base64Utils;
import com.googlecode.gwt.crypto.bouncycastle.AsymmetricBlockCipher;
import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.bouncycastle.encodings.PKCS1Encoding;
import com.googlecode.gwt.crypto.bouncycastle.engines.RSAEngine;
import com.googlecode.gwt.crypto.bouncycastle.params.RSAKeyParameters;

import net.inveed.gwt.editor.client.model.properties.BooleanPropertyModel;
import net.inveed.gwt.editor.client.model.properties.DurationPropertyModel;
import net.inveed.gwt.editor.client.model.properties.EntityReferencePropertyModel;
import net.inveed.gwt.editor.client.model.properties.EnumPropertyModel;
import net.inveed.gwt.editor.client.model.properties.FloatPropertyModel;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.client.model.properties.IPropertyInstantiator;
import net.inveed.gwt.editor.client.model.properties.IntegerFieldModel;
import net.inveed.gwt.editor.client.model.properties.IntegerIDPropertyModel;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.client.model.properties.BinaryPropertyModel;
import net.inveed.gwt.editor.client.model.properties.StringIDPropertyModel;
import net.inveed.gwt.editor.client.model.properties.TextPropertyModel;
import net.inveed.gwt.editor.client.model.properties.TimestampPropertyModel;
import net.inveed.gwt.editor.client.utils.CryptoHelper;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.shared.EntityModelDTO;
import net.inveed.gwt.editor.shared.EnumModelDTO;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.BinaryPropertyDTO;
import net.inveed.gwt.editor.shared.properties.BooleanPropertyDTO;
import net.inveed.gwt.editor.shared.properties.DateTimePropertyDTO;
import net.inveed.gwt.editor.shared.properties.DurationPropertyDTO;
import net.inveed.gwt.editor.shared.properties.EntityListPropertyDTO;
import net.inveed.gwt.editor.shared.properties.EnumPropertyDTO;
import net.inveed.gwt.editor.shared.properties.FloatPropertyDTO;
import net.inveed.gwt.editor.shared.properties.IntegerIdPropertyDTO;
import net.inveed.gwt.editor.shared.properties.IntegerPropertyDTO;
import net.inveed.gwt.editor.shared.properties.ObjectRefPropertyDTO;
import net.inveed.gwt.editor.shared.properties.StringIdPropertyDTO;
import net.inveed.gwt.editor.shared.properties.TextPropertyDTO;

public class ConfigurationRegistry {	
	public static final ConfigurationRegistry INSTANCE = new ConfigurationRegistry();
	private static Logger LOG = Logger.getLogger(ConfigurationRegistry.class.getName());
	
	private final HashMap<Class<?>, IPropertyInstantiator<?, ?>> propertyTypes;
	private final HashMap<String, EntityModel> entityModels;
	private final HashMap<String, EnumModel> enums;
	
	private byte[] skClear;
	private byte[] skEncrypted;
	
	//private ServerEnvironmentDTO environment;
	private String jsonRpcUrl;
	private String modelUrl;
	private long timeOffset;
	
	private ConfigurationRegistry() {
		this.propertyTypes 	= new HashMap<>();
		this.entityModels 	= new HashMap<>();
		this.enums 			= new HashMap<>();
		LOG = Logger.getLogger(ConfigurationRegistry.class.getName());
		this.registerBasicPropertyTypes();
	}
	
	public void registerPropertyType(Class<? extends AbstractPropertyDTO> dtoType, IPropertyInstantiator<?, ?> instantiator) {
		LOG.fine("Registering '" + dtoType.getName() +"' property type");;
		this.propertyTypes.put(dtoType, instantiator);
	}
	
	private void registerBasicPropertyTypes() {
		LOG.info("Registering base property types...");
		registerPropertyType(BooleanPropertyDTO.class, new IPropertyInstantiator<BooleanPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(BooleanPropertyDTO dto, String name, EntityModel entityModel) {
				return new BooleanPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(BinaryPropertyDTO.class, new IPropertyInstantiator<BinaryPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(BinaryPropertyDTO dto, String name, EntityModel entityModel) {
				return new BinaryPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(DateTimePropertyDTO.class, new IPropertyInstantiator<DateTimePropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(DateTimePropertyDTO dto, String name, EntityModel entityModel) {
				return new TimestampPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(DurationPropertyDTO.class, new IPropertyInstantiator<DurationPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(DurationPropertyDTO dto, String name, EntityModel entityModel) {
				return new DurationPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(EntityListPropertyDTO.class, new IPropertyInstantiator<EntityListPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(EntityListPropertyDTO dto, String name, EntityModel entityModel) {
				return new LinkedEntitiesListPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(EnumPropertyDTO.class, new IPropertyInstantiator<EnumPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(EnumPropertyDTO dto, String name, EntityModel entityModel) {
				return new EnumPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(FloatPropertyDTO.class, new IPropertyInstantiator<FloatPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(FloatPropertyDTO dto, String name, EntityModel entityModel) {
				return new FloatPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(IntegerIdPropertyDTO.class, new IPropertyInstantiator<IntegerIdPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(IntegerIdPropertyDTO dto, String name, EntityModel entityModel) {
				return new IntegerIDPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(IntegerPropertyDTO.class, new IPropertyInstantiator<IntegerPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(IntegerPropertyDTO dto, String name, EntityModel entityModel) {
				return new IntegerFieldModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(ObjectRefPropertyDTO.class, new IPropertyInstantiator<ObjectRefPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(ObjectRefPropertyDTO dto, String name, EntityModel entityModel) {
				return new EntityReferencePropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(StringIdPropertyDTO.class, new IPropertyInstantiator<StringIdPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(StringIdPropertyDTO dto, String name, EntityModel entityModel) {
				return new StringIDPropertyModel(dto, name, entityModel);
			}
		});
		
		registerPropertyType(TextPropertyDTO.class, new IPropertyInstantiator<TextPropertyDTO, IPropertyDescriptor<?>>() {
			@Override
			public IPropertyDescriptor<?> create(TextPropertyDTO dto, String name, EntityModel entityModel) {
				return new TextPropertyModel(dto, name, entityModel);
			}
		});
		LOG.info("Base property types was registered");
	}
	
	public <T extends AbstractPropertyDTO> IPropertyDescriptor<?> createPropertyDesc(T dto, String name, EntityModel entityModel) {
		if (dto == null) {
			LOG.warning("Cannot create propery: DTO is NULL");
			throw new NullPointerException("dto");
		}
		if (name == null) {
			LOG.warning("Cannot create propery: NAME is NULL");
			throw new NullPointerException("name");
		}
		if (entityModel == null) {
			LOG.warning("Cannot create propery: EntityModel is NULL");
			throw new NullPointerException("entityModel");
		}
		
		@SuppressWarnings("unchecked")
		IPropertyInstantiator<T, ?> i = (IPropertyInstantiator<T, ?>) this.propertyTypes.get(dto.getClass());
		if (i == null) {
			LOG.warning("Cannot create propery: Property type for DTO '" + dto.getClass() +"' was not registered");
			return null;
		} else {
			LOG.fine("Trying to create property '" + name +"' for DTO type " + dto.getClass() + "'");
			return i.create(dto, name, entityModel);
		}
	}
	
	public EntityModel getModel(String name) {
		return this.entityModels.get(name);
	}
	
	public Collection<EntityModel> getModels() {
		return this.entityModels.values();
	}
	
	public EnumModel getEnum(String name) {
		return this.enums.get(name);
	}
	
	public Promise<Void, IError> updateServerEnvironment() {
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		
		ServerEnvironmentLoader seLoader = new ServerEnvironmentLoader();
		Promise<Void, IError> sePromise = seLoader.load();
		sePromise.thenApply((v1)->{
			this.onEnvironmentLoaded(seLoader);
			ret.complete(null);
			return null;
		});
		sePromise.onError((e,t)->{
			ret.error(e, t);
			return null;
		});
		
		return ret;
	}
	public Promise<Void, IError> loadModel() {
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		
		Promise<Void, IError> sePromise = updateServerEnvironment();
		sePromise.thenApply((v1)->{
			ModelLoader loader = new ModelLoader();
			Promise<Void, IError> mlPromise = loader.load(this.modelUrl);
			mlPromise.thenApply((v2)->{
				onModelDTOLoaded(loader);
				ret.complete(null);
				return null;
			});
			mlPromise.onError((e,t)->{
				ret.error(e, t);
				return null;
			});
			return null;
		});
		sePromise.onError((e,t)->{
			ret.error(e, t);
			return null;
		});
		
		return ret;
	}
	
	public String getJsonRPCUrl() {
		return this.jsonRpcUrl;
	}
	
	private void onEnvironmentLoaded(ServerEnvironmentLoader loader) {
		this.jsonRpcUrl = loader.getJsonRPCUrl();
		this.modelUrl   = loader.getModelUrl();
		this.timeOffset = System.currentTimeMillis() - loader.getEnvironmentDTO().time;
		if (loader.getEnvironmentDTO().pk != null) {
			byte[] rsaPub = Base64Utils.fromBase64(loader.getEnvironmentDTO().pk);
			initializeSK(rsaPub);
		}
	}
	
	public long getServerTimeMills() {
		return System.currentTimeMillis() - this.timeOffset;
	}
	
	private void onModelDTOLoaded(ModelLoader loader) {
		if (loader == null) {
			return;
		}
		for (EnumModelDTO en : loader.getEnumModelDTOs().values()) {
			EnumModel model = new EnumModel(en, this);
			enums.put(model.getName(), model);
		}
		for (EntityModelDTO em : loader.getEnitiyModelDTOs().values()) {
			EntityModel model = new EntityModel(em, this);
			this.entityModels.put(model.getEntityName(), model);
		}
		
		for (EntityModel em : this.entityModels.values()) {
			em.initialize();
			//TODO: Validate
		}
	}
	
	public byte[] encrypt(byte[] data) {
		try {
			return CryptoHelper.encryptAESCBCPKCS7(data, this.skClear, 0, 16, this.skClear, 16, 16);
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			return null;
		}
	}
	public byte[] getSKEncrypted() {
		return this.skEncrypted;
	}
	
	private void initializeSK(byte[] publicRsaKey) {
		this.skClear = CryptoHelper.generateRandomSeed(32);
		byte[] mod = new byte[129]; //for 1024-bit key
		byte[] exp = new byte[publicRsaKey.length - mod.length];
		System.arraycopy(publicRsaKey, 0, mod, 0, mod.length);
		System.arraycopy(publicRsaKey, mod.length, exp, 0, exp.length);
		BigInteger modulus = new BigInteger(mod);
		BigInteger exponent = new BigInteger(exp);
		
		RSAKeyParameters pubKey = new RSAKeyParameters(false, modulus, exponent);
		
		AsymmetricBlockCipher eng = new PKCS1Encoding(new RSAEngine());
		eng.init(true, pubKey);
		if (eng.getOutputBlockSize() != ((PKCS1Encoding)eng).getUnderlyingCipher().getOutputBlockSize()) 
        {
			return;
            //fail("PKCS1 output block size incorrect"); 
        }
		try {
			this.skEncrypted = eng.processBlock(this.skClear, 0, this.skClear.length);
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}
	}
}
