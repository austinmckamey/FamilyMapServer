package famMapServer.services;

import com.google.gson.Gson;
import famMapServer.data.*;
import famMapServer.dataAccess.*;
import models.Event;
import models.Person;
import models.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Random;

/**
 * Generates family for different services
 */
public class GenerateFamily extends Service {
    /** Connection to Database */
    private final Connection conn;
    /** Object containing female names */
    private FemaleData femaleData;
    /** Object containing locations */
    private LocationData locationData;
    /** Object containing male names */
    private MaleData maleData;
    /** Object containing surnames */
    private SurnameData surnameData;
    /** Initial generation, allows user to be identified */
    private int initGeneration = 0;

    /**
     * Constructor
     * @param conn connection to Database
     */
    public GenerateFamily(Connection conn) {
        this.conn = conn;
    }

    /**
     * Extracts data from files and loads them into the Objects
     * @param user User whose family tree will be generated
     * @param generations Number of generations to generate
     * @throws FileNotFoundException
     * @throws DataAccessException
     */
    public void fillGenerations(User user, int generations) throws FileNotFoundException, DataAccessException {
        Gson gson = new Gson();
        Reader reader = new FileReader("json/locations.json");
        locationData = gson.fromJson(reader, LocationData.class);
        reader = new FileReader("json/fnames.json");
        femaleData = gson.fromJson(reader, FemaleData.class);
        reader = new FileReader("json/mnames.json");
        maleData = gson.fromJson(reader, MaleData.class);
        reader = new FileReader("json/snames.json");
        surnameData = gson.fromJson(reader, SurnameData.class);

        String username = user.getUsername();
        initGeneration = generations;
        int initYear = 2000;
        generatePerson(username,user.getGender(),generations, initYear);
    }

    /**
     * Recursive function to generate people in family tree
     * @param username Username of User that tree is generated for
     * @param gender Gender of person being generated
     * @param generations Number of generations to continue generating
     * @param year Year that events occur for people being generated
     * @return Person object that is generated
     * @throws DataAccessException
     */
    private Person generatePerson(String username, String gender, int generations, int year) throws DataAccessException {
        Random generator = new Random();
        Location[] locData = locationData.getData();
        String[] femData = femaleData.getData();
        String[] malData = maleData.getData();
        String[] surData = surnameData.getData();
        int randomIndex;

        EventDAO eDao = new EventDAO(conn);
        PersonDAO pDao = new PersonDAO(conn);
        UserDAO uDao = new UserDAO(conn);

        Person mother = null;
        Person father = null;

        if(generations > 1) {
            mother = generatePerson(username,"f",generations - 1,year - 23);
            father = generatePerson(username,"m",generations - 1,year - 23);

            pDao.delete(mother.getPersonID());
            pDao.delete(father.getPersonID());

            mother.setSpouseID(father.getPersonID());
            father.setSpouseID(mother.getPersonID());

            pDao.insert(mother);
            pDao.insert(father);

            randomIndex = generator.nextInt(locData.length);
            Location location = locData[randomIndex];

            Event marriage1 = new Event(createToken(),username,mother.getPersonID(),location.getLatitude(),location.getLongitude(),
                    location.getCountry(),location.getCity(),"Marriage",year - 3);
            Event marriage2 = new Event(createToken(),username,father.getPersonID(),location.getLatitude(),location.getLongitude(),
                    location.getCountry(),location.getCity(),"Marriage",year - 3);

            eDao.insert(marriage1);
            eDao.insert(marriage2);
        }
        User user = uDao.find(username);
        Person person;
        randomIndex = generator.nextInt(surData.length);
        String surname = surData[randomIndex];

        if (generations == initGeneration) {
            person = new Person(user.getPersonID(),username,user.getFirstName(),user.getLastName(),
                    user.getGender(),mother.getPersonID(), father.getPersonID(), null);
        } else if (gender.equals("m")) {
            randomIndex = generator.nextInt(malData.length);
            String male = malData[randomIndex];
            if(mother == null) {
                person = new Person(createToken(), username, male, surname, gender,
                        null, null, null);
            } else {
                person = new Person(createToken(), username, male, surname, gender,
                        mother.getPersonID(), father.getPersonID(), null);
            }
        } else {
            randomIndex = generator.nextInt(femData.length);
            String female = femData[randomIndex];
            if(mother == null) {
                person = new Person(createToken(), username, female, surname, gender,
                        null, null, null);
            } else {
                person = new Person(createToken(), username, female, surname, gender,
                        mother.getPersonID(), father.getPersonID(), null);
            }
        }

        randomIndex = generator.nextInt(locData.length);
        Location location = locData[randomIndex];

        Event birth = new Event(createToken(),username,person.getPersonID(),location.getLatitude(),location.getLongitude(),
                location.getCountry(),location.getCity(),"Birth",year);

        randomIndex = generator.nextInt(locData.length);
        location = locData[randomIndex];

        Event death = new Event(createToken(),username,person.getPersonID(),location.getLatitude(),location.getLongitude(),
                location.getCountry(),location.getCity(),"Death",year + 80);

        pDao.insert(person);
        eDao.insert(birth);
        eDao.insert(death);

        return person;
    }
}
