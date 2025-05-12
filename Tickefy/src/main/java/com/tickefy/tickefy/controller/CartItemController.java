package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.CartItem;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.response.JsonResponse;
import com.tickefy.tickefy.service.CartItemService;
import com.tickefy.tickefy.service.UserService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartItemController {

    private final CartItemService cartItemService;

    private final UserService userService;

    @Autowired
    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String jwt,
                                       @RequestParam("homeTeamName") @NotBlank(message = "Home team name is required.") String homeTeamName,
                                       @RequestParam("awayTeamName") @NotBlank(message = "Away team name is required.") String awayTeamName,
                                       @RequestParam("homeTeamLogo") @NotBlank(message = "Home team name is required.") String homeTeamLogo,
                                       @RequestParam("awayTeamLogo") @NotBlank(message = "Away team name is required.") String awayTeamLogo,
                                       @RequestParam("seatNumber") @Min(value = 1, message = "Seat number must be greater than 0.") int seatNumber,
                                       @RequestParam("VenueName") @NotBlank(message = "Venue name is required.") String stadiumName,
                                       @RequestParam("VenueCity") @NotBlank(message = "Venue city is required.") String stadiumCity,
                                       @RequestParam("matchDate") @NotBlank(message = "Match date is required.") String matchDate) {

       // LocalDateTime date = LocalDateTime.parse(matchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime date;
        try {
            date = LocalDateTime.parse(matchDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Invalid match date format. Please use yyyy-MM-dd'T'HH:mm.");
        }

        Client client = (Client) userService.getProfile(jwt);
       // String matchName = homeTeamName + " VS " + awayTeamName;

        if(cartItemService.isSeatAvailable(seatNumber, stadiumName, stadiumCity, date))
            throw new ConflictException("Seat number " +seatNumber+ " is already Occupied. Pick another seat");

        try{

            CartItem cartItem = cartItemService.addToCart(client.getId(), homeTeamName,
                    awayTeamName ,homeTeamLogo,awayTeamLogo , seatNumber, stadiumName, stadiumCity, date);
            return ResponseEntity.ok(cartItem);

        } catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new JsonResponse("adding Item to Cart Error : "+e.getMessage())
                    , HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestHeader("Authorization") String jwt) {

        Client client = (Client) userService.getProfile(jwt);

        return ResponseEntity.ok(cartItemService.getClientCart(client.getId()));
    }

    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItem> getCartItemById(@RequestHeader("Authorization") String jwt
                                                   ,@PathVariable UUID cartItemId) {

        Client client = (Client) userService.getProfile(jwt);

       CartItem cartItem = cartItemService.getCartItemById(client.getId(), cartItemId);

        return new ResponseEntity<>(cartItem,HttpStatus.OK);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@RequestHeader("Authorization") String jwt
                                              ,@PathVariable UUID cartItemId) {

        Client client = (Client) userService.getProfile(jwt);

        cartItemService.removeCartItem(client.getId(), cartItemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/check-seat")
    public ResponseEntity<JsonResponse> checkSeat(@RequestHeader("Authorization") String jwt,
                                             @RequestParam("seatNumber") @Min(value = 1, message = "Seat number must be greater than 0.") int seatNumber,
                                             @RequestParam("VenueName") @NotBlank(message = "Venue name is required.") String stadiumName,
                                             @RequestParam("VenueCity") @NotBlank(message = "Venue city is required.") String stadiumCity,
                                             @RequestParam("matchDate") @NotBlank(message = "Match date is required.") String matchDate) {

        //LocalDateTime date = LocalDateTime.parse(matchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime date;
        try {
            date = LocalDateTime.parse(matchDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Invalid match date format. Please use yyyy-MM-dd'T'HH:mm.");
        }

        try{

            if(cartItemService.isSeatAvailable(seatNumber, stadiumName, stadiumCity, date))
                return new ResponseEntity<>(new JsonResponse("Seat number " +seatNumber+ " is already Occupied. Pick another seat")
                        ,HttpStatus.CONFLICT);

            return new ResponseEntity<>(new JsonResponse("Seat number "+seatNumber+" is Available."),HttpStatus.OK);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new JsonResponse("Error checking seat availability : "+e.getMessage())
                    ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
