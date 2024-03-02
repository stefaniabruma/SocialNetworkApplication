/*
package ir.map.socialnetworkapp.UserInterface;


import ir.map.socialnetworkapp.Domain.*;
import ir.map.socialnetworkapp.Domain.Validation.ValidationException;
import ir.map.socialnetworkapp.Repository.DBRepositories.FriendshipDBRepository;
import ir.map.socialnetworkapp.Repository.DBRepositories.UserDBRepository;
import ir.map.socialnetworkapp.Repository.InMemory.InMemoryRepository;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.PagingRepository;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;
import ir.map.socialnetworkapp.Service.NotFoundException;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Service.ServiceDB;
import ir.map.socialnetworkapp.Service.ServiceInMemory;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UI {
    Service serv;

    List<String> menu = Arrays.asList("1 - Show All Users", "2 - Add User", "3 - Remove User", "4 - Show All Friendships", "5 - Add Friendship", "6 - Remove Friendship", "7 - Number of Communities", "8 - The Most Sociable Community",  "9 - Friends from a given month for a user");

    public UI(PagingRepository<Long, User> r1, Repository<Tuple<Long, Long>, Friendship> r2, Repository<Tuple<Long, Long>, FriendshipRequest> r3, Repository<Long, Message> r4) {
        if(r1.getClass() == InMemoryRepository.class && r2.getClass() == InMemoryRepository.class)
            this.serv = new ServiceInMemory(r1, r2, r3, r4);
        else if (r1.getClass() == UserDBRepository.class && r2.getClass() == FriendshipDBRepository.class) {
            this.serv = new ServiceDB(r1, r2, r3, r4);
        }
        else{
            throw new IllegalArgumentException("Incompatible types of repos!\nUsage: UserRepository + FriendshipRepository from the same Repo Family");
        }
    }

    public void printMenu(){
        menu.forEach(System.out::println);
    }

    public void run(){
        while(true) {
            printMenu();
            Scanner s = new Scanner(System.in);
            int cmd = s.nextInt();

            switch (cmd) {
                case 0:
                    return;
                case 1:
                    showAllUsers();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    removeUser();
                    break;
                case 4:
                    showAllFriendships();
                    break;
                case 5:
                    addFriendship();
                    break;
                case 6:
                    removeFriendship();
                   break;
                case 7:
                    communitiesNumber();
                    break;
                case 8:
                    mostSociable();
                    break;
                case 9:
                    friendsFromMonth();
                    break;
            }
        }
    }

    public void showAllUsers(){
        Iterable<User> all = serv.showAllUsers();
        all.forEach(System.out::println);
    }

    public void addUser()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("First Name:");
        String firstName = s.nextLine();
        System.out.println("Last Name:");
        String lastName = s.nextLine();

        try{
            serv.addUser(firstName, lastName);
        }
        catch (ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    public void removeUser(){
        System.out.println("Id:");
        Scanner s = new Scanner(System.in);
        Long id = s.nextLong();
        serv.removeUser(id);
    }

    public void showAllFriendships(){
        Iterable<Friendship> fs = serv.showAllFriendships();
        fs.forEach(System.out::println);
    }

    public void addFriendship(){
        Scanner s = new Scanner(System.in);
        System.out.println("User1:");
        Long id1 = s.nextLong();
        System.out.println("User2:");
        Long id2 = s.nextLong();

        try{
            serv.addFriendship(id1, id2);
        } catch (NotFoundException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    public void removeFriendship(){
        Scanner s = new Scanner(System.in);
        System.out.println("User1:");
        Long id1 = s.nextLong();
        System.out.println("User2:");
        Long id2 = s.nextLong();

        serv.removeFriendship(id1, id2);
    }

    public void communitiesNumber(){
        System.out.println(serv.communitiesNumber());
    }

    public void mostSociable(){
        serv.mostSociable().forEach(System.out::println);
    }

    public void friendsFromMonth(){
        Scanner s = new Scanner(System.in);
        System.out.println("User:");
        Long id = s.nextLong();
        System.out.println("Month:");
        int month = s.nextInt();

        serv.friendsFromMonth(id, month).forEach(System.out::println);
    }
}
*/
