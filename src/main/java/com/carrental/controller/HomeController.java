package com.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.carrental.model.Car;
import com.carrental.model.Customer;
import com.carrental.model.Rental;

import com.carrental.service.CarService;
import com.carrental.service.CustomerService;
import com.carrental.service.RentalService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;


    // =====================================================
    // LOGIN PAGE
    // =====================================================

    @GetMapping("/")
    public String home() {
        return "login";
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        // ADMIN LOGIN
        if (username.equals("admin") && password.equals("admin123")) {

            session.setAttribute("role", "ADMIN");

            return "redirect:/dashboard";
        }

        // CUSTOMER LOGIN
        Customer customer = customerService.login(username, password);

        if (customer != null) {

            session.setAttribute("role", "CUSTOMER");
            session.setAttribute("customerId", customer.getId());
            session.setAttribute("customerName", customer.getName());

            return "redirect:/customerhome";
        }

        // INVALID LOGIN
        return "redirect:/";
    }


    // =====================================================
    // ADMIN DASHBOARD
    // =====================================================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        return "dashboard";
    }


    // =====================================================
    // CUSTOMER HOME
    // =====================================================

    @GetMapping("/customerhome")
    public String customerHome(Model model,
                               HttpSession session) {

        if (!isCustomer(session)) {
            return "redirect:/";
        }

        model.addAttribute(
                "cars",
                carService.getAllCars()
        );

        return "customerhome";
    }


    // =====================================================
    // ADD CAR
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/addcar")
    public String addCarPage(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        return "addcar";
    }


    // SAVE CAR
    @PostMapping("/savecar")
    public String saveCar(@RequestParam String brand,
                          @RequestParam String model,
                          @RequestParam double pricePerDay,
                          HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        Car car = new Car();

        car.setBrand(brand);
        car.setModel(model);
        car.setPricePerDay(pricePerDay);

        carService.saveCar(car);

        return "redirect:/dashboard";
    }


    // =====================================================
    // VIEW CARS
    // ADMIN / CUSTOMER
    // =====================================================

    @GetMapping("/viewcars")
    public String viewCars(Model model,
                           HttpSession session) {

        if (!isAdmin(session) && !isCustomer(session)) {
            return "redirect:/";
        }

        model.addAttribute(
                "cars",
                carService.getAllCars()
        );

        return "viewcars";
    }


    // =====================================================
    // DELETE CAR
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/deletecar")
    public String deleteCarPage(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        return "deletecar";
    }


    @PostMapping("/deletecar")
    public String deleteCar(@RequestParam int carId,
                            Model model,
                            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        carService.deleteCar(carId);

        model.addAttribute(
                "message",
                "Car Deleted Successfully!"
        );

        model.addAttribute(
                "cars",
                carService.getAllCars()
        );

        return "viewcars";
    }


    // =====================================================
    // SEARCH CAR
    // ADMIN / CUSTOMER
    // =====================================================

    @GetMapping("/searchcar")
    public String searchCarPage(HttpSession session) {

        if (!isAdmin(session) && !isCustomer(session)) {
            return "redirect:/";
        }

        return "searchcar";
    }


    @PostMapping("/searchcar")
    public String searchCar(@RequestParam String brand,
                            Model model,
                            HttpSession session) {

        if (!isAdmin(session) && !isCustomer(session)) {
            return "redirect:/";
        }

        model.addAttribute(
                "cars",
                carService.searchCars(brand)
        );

        return "viewcars";
    }


    // =====================================================
    // UPDATE CAR
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/updatecar")
    public String updateCarPage(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        return "updatecar";
    }


    @PostMapping("/updatecar")
    public String updateCar(@RequestParam int id,
                            @RequestParam String brand,
                            @RequestParam String model,
                            @RequestParam double pricePerDay,
                            Model modelObj,
                            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        Car car = carService.getCarById(id);

        if (car != null) {

            car.setBrand(brand);
            car.setModel(model);
            car.setPricePerDay(pricePerDay);

            carService.updateCar(car);

            modelObj.addAttribute(
                    "message",
                    "Car Updated Successfully!"
            );
        }

        modelObj.addAttribute(
                "cars",
                carService.getAllCars()
        );

        return "viewcars";
    }


    // =====================================================
    // ADD CUSTOMER
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/addcustomer")
    public String addCustomerPage(HttpSession session) {

        // Allow customer registration from login page
        if (session.getAttribute("role") == null) {
            return "addcustomer";
        }

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        return "addcustomer";
    }


    // SAVE CUSTOMER
    @PostMapping("/savecustomer")
    public String saveCustomer(@RequestParam String name,
                               @RequestParam String phone,
                               @RequestParam String email,
                               @RequestParam String license,
                               @RequestParam String username,
                               @RequestParam String password) {

        Customer customer = new Customer();

        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setLicense(license);
        customer.setUsername(username);
        customer.setPassword(password);

        customerService.saveCustomer(customer);

        return "redirect:/";
    }


    // =====================================================
    // VIEW CUSTOMERS
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/viewcustomers")
    public String viewCustomers(Model model,
                                HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        model.addAttribute(
                "customers",
                customerService.getAllCustomers()
        );

        return "viewcustomers";
    }


    // =====================================================
    // RENT CAR
    // ADMIN / CUSTOMER
    // =====================================================

    @GetMapping("/rentcar")
    public String rentCarPage(HttpSession session) {

        if (!isAdmin(session) && !isCustomer(session)) {
            return "redirect:/";
        }

        return "rentcar";
    }


    // SAVE RENTAL
    @PostMapping("/saverental")
    public String saveRental(@RequestParam int customerId,
                             @RequestParam int carId,
                             @RequestParam int days,
                             HttpSession session) {

        if (!isAdmin(session) && !isCustomer(session)) {
            return "redirect:/";
        }

        Rental rental = new Rental();

        rental.setCustomerId(customerId);
        rental.setCarId(carId);
        rental.setDays(days);

        double amount = days * 2500;

        rental.setTotalAmount(amount);
        rental.setStatus("RENTED");

        rental.setRentDate(
                java.time.LocalDate.now().toString()
        );

        rentalService.saveRental(rental);

        carService.updateCarStatus(
                carId,
                "RENTED"
        );

        if (isCustomer(session)) {
            return "redirect:/customerhome";
        }

        return "redirect:/dashboard";
    }


    // =====================================================
    // VIEW RENTALS
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/viewrentals")
    public String viewRentals(Model model,
                              HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        model.addAttribute(
                "rentals",
                rentalService.getAllRentals()
        );

        return "viewrentals";
    }


    // =====================================================
    // RETURN CAR
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/returncar")
    public String returnCarPage(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        return "returncar";
    }


    @PostMapping("/returncar")
    public String returnCar(@RequestParam int rentalId,
                            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/customerhome";
        }

        Rental rental =
                rentalService.getRentalById(rentalId);

        if (rental != null) {

            rental.setStatus("RETURNED");

            rental.setReturnDate(
                    java.time.LocalDate.now().toString()
            );

            rentalService.updateRental(rental);

            carService.updateCarStatus(
                    rental.getCarId(),
                    "AVAILABLE"
            );
        }

        return "redirect:/dashboard";
    }


    // =====================================================
    // RECEIPT
    // =====================================================

    @GetMapping("/receipt")
    public String receipt(@RequestParam int rentalId,
                          Model model,
                          HttpSession session) {

        if (!isAdmin(session) && !isCustomer(session)) {
            return "redirect:/";
        }

        Rental rental =
                rentalService.getRentalById(rentalId);

        model.addAttribute(
                "rental",
                rental
        );

        return "receipt";
    }


    // =====================================================
    // LOGOUT
    // =====================================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }


    // =====================================================
    // ROLE CHECK METHODS
    // =====================================================

    private boolean isAdmin(HttpSession session) {

        return "ADMIN".equals(
                session.getAttribute("role")
        );
    }


    private boolean isCustomer(HttpSession session) {

        return "CUSTOMER".equals(
                session.getAttribute("role")
        );
    }

}