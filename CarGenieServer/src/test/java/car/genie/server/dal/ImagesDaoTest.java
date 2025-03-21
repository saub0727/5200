//package car.genie.server.dal;
//
//import car.genie.server.model.Images;
//import car.genie.server.model.Vehicles;
//import car.genie.server.model.Models;
//import car.genie.server.model.Manufacturers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.AfterEach;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ImagesDaoTest {
//
//    private ImagesDao imagesDao;
//    private VehiclesDao vehiclesDao;
//    private ModelsDao modelsDao;
//    private ManufacturersDao manufacturersDao;
//    private Manufacturers testManufacturer;
//    private Models testModel;
//    private Vehicles testVehicle;
//    private Images testImage;
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        imagesDao = ImagesDao.getInstance();
//        vehiclesDao = VehiclesDao.getInstance();
//        modelsDao = ModelsDao.getInstance();
//        manufacturersDao = ManufacturersDao.getInstance();
//
//        testManufacturer = Manufacturers.builder()
//                .manufacturerName("TestManufacturer")
//                .build();
//        testManufacturer = manufacturersDao.create(testManufacturer);
//        assertNotNull(testManufacturer.getManufacturerId(), "Manufacturer ID should not be null");
//
//        testModel = Models.builder()
//                .modelName("TestModel")
//                .manufacturerId(testManufacturer.getManufacturerId())
//                .build();
//        testModel = modelsDao.create(testModel);
//        assertNotNull(testModel.getModelId(), "Model ID should not be null");
//
//        testVehicle = Vehicles.builder()
//                .vehicleId(1L)
//                .vin("TEST123456789")
//                .price(30000)
//                .postingDate(LocalDate.now())
//                .description("Test Vehicle")
//                .modelId(testModel.getModelId())
//                .build();
//        testVehicle = vehiclesDao.create(testVehicle);
//        assertNotNull(testVehicle.getVehicleId(), "Vehicle ID should not be null");
//
//        testImage = Images.builder()
//                .vehicleId(testVehicle.getVehicleId())
//                .imageURL("http://example.com/image.jpg")
//                .build();
//    }
//
//    @AfterEach
//    public void tearDown() throws SQLException {
//        try {
//            List<Images> images = imagesDao.getImagesByVehicleId(testVehicle.getVehicleId());
//            for (Images image : images) {
//                imagesDao.delete(image);
//            }
//        } catch (SQLException e) {
//            // Ignore
//        }
//
//        if (testVehicle != null) {
//            vehiclesDao.delete(testVehicle);
//        }
//
//        if (testModel != null && testModel.getModelId() != null) {
//            modelsDao.delete(testModel);
//        }
//
//        if (testManufacturer != null && testManufacturer.getManufacturerId() != null) {
//            manufacturersDao.delete(testManufacturer);
//        }
//    }
//
//    @Test
//    public void testCreate() throws SQLException {
//        Images createdImage = imagesDao.create(testImage);
//
//        assertNotNull(createdImage);
//        assertNotNull(createdImage.getImageId());
//        assertEquals(testVehicle.getVehicleId(), createdImage.getVehicleId());
//        assertEquals("http://example.com/image.jpg", createdImage.getImageURL());
//    }
//
//    @Test
//    public void testGetImageById() throws SQLException {
//        Images createdImage = imagesDao.create(testImage);
//
//        Images retrievedImage = imagesDao.getImageById(createdImage.getImageId());
//
//        assertNotNull(retrievedImage);
//        assertEquals(createdImage.getImageId(), retrievedImage.getImageId());
//        assertEquals(testVehicle.getVehicleId(), retrievedImage.getVehicleId());
//        assertEquals("http://example.com/image.jpg", retrievedImage.getImageURL());
//    }
//
//    @Test
//    public void testGetImagesByVehicleId() throws SQLException {
//        imagesDao.create(testImage);
//
//        Images secondImage = Images.builder()
//                .vehicleId(testVehicle.getVehicleId())
//                .imageURL("http://example.com/image2.jpg")
//                .build();
//        imagesDao.create(secondImage);
//
//        List<Images> imagesList = imagesDao.getImagesByVehicleId(testVehicle.getVehicleId());
//
//        assertNotNull(imagesList);
//        assertEquals(2, imagesList.size());
//        assertTrue(imagesList.stream().anyMatch(i -> i.getImageURL().equals("http://example.com/image.jpg")));
//        assertTrue(imagesList.stream().anyMatch(i -> i.getImageURL().equals("http://example.com/image2.jpg")));
//    }
//
//    @Test
//    public void testUpdateImage() throws SQLException {
//        Images createdImage = imagesDao.create(testImage);
//
//        createdImage.setImageURL("http://example.com/updated-image.jpg");
//
//        Images updatedImage = imagesDao.updateImage(createdImage);
//
//        assertNotNull(updatedImage);
//        assertEquals("http://example.com/updated-image.jpg", updatedImage.getImageURL());
//
//        Images retrievedImage = imagesDao.getImageById(createdImage.getImageId());
//        assertEquals("http://example.com/updated-image.jpg", retrievedImage.getImageURL());
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        Images createdImage = imagesDao.create(testImage);
//
//        imagesDao.delete(createdImage);
//
//        Images retrievedImage = imagesDao.getImageById(createdImage.getImageId());
//        assertNull(retrievedImage);
//    }
//
//    @Test
//    public void testDeleteImagesByVehicleId() throws SQLException {
//        imagesDao.create(testImage);
//
//        Images secondImage = Images.builder()
//                .vehicleId(testVehicle.getVehicleId())
//                .imageURL("http://example.com/image2.jpg")
//                .build();
//        imagesDao.create(secondImage);
//
//        imagesDao.deleteImagesByVehicleId(testVehicle.getVehicleId());
//
//        List<Images> imagesList = imagesDao.getImagesByVehicleId(testVehicle.getVehicleId());
//        assertTrue(imagesList.isEmpty());
//    }
//}
