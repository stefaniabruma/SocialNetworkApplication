package ir.map.socialnetworkapp.Service;


import ir.map.socialnetworkapp.Domain.*;
import ir.map.socialnetworkapp.Domain.Validation.*;
import ir.map.socialnetworkapp.Repository.PagingRepositories.FriendshipRequestPagingDBRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.MessageDBPagingRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.UserPagingDBRepository;
import ir.map.socialnetworkapp.Repository.PagingUtils.Page;
import ir.map.socialnetworkapp.Repository.PagingUtils.PageObject;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;
import ir.map.socialnetworkapp.Utils.Observable;
import ir.map.socialnetworkapp.Utils.Observer;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public abstract class Service implements Observable {

    protected UserPagingDBRepository repo_user;
    protected Repository<Tuple<Long,Long>, Friendship> repo_friendship;
    protected FriendshipRequestPagingDBRepository repo_friendshipreq;
    protected MessageDBPagingRepository repo_message;
    protected Validator<User> v_user = new UserValidator();
    protected Validator<Friendship> v_friendship = new FriendshipValidator();
    protected Validator<FriendshipRequest> v_friendshipreq = new FriendshipRequestValidator();

    private List<Observer> observers = new ArrayList<>();
    private static Long currID = 1L;

    public Service(UserPagingDBRepository repo_user, Repository<Tuple<Long, Long>, Friendship> repo_friendship, FriendshipRequestPagingDBRepository repo_friendhsipreq, MessageDBPagingRepository repo_message) {

        this.repo_user = repo_user;
        this.repo_friendship = repo_friendship;
        this.repo_friendshipreq = repo_friendhsipreq;
        this.repo_message = repo_message;

    }

    protected Long nextUserID(){
        currID += 1;
        return currID - 1;
    }

    private void dfs_visit_nr_com(Map<Long, Integer> color, Map<Long, Long> parent, User user) {

        color.put(user.getId(), 1);

        if(repo_user.findOne(user.getId()).isPresent())
            user = repo_user.findOne(user.getId()).get();

        User finalUser = user;
        user.getFriends().forEach(friend -> {
            if (color.get(friend.getId()) == 0) {
                parent.put(friend.getId(), finalUser.getId());
                dfs_visit_nr_com(color, parent, friend);
            }
        });
        color.put(user.getId(), 2);
    }


    private void dfs_visit_sociable(Map<Long, Integer> color, Map<Long, Long> parent, Map<Long, Integer> distance, User user) {

        color.put(user.getId(), 1);

        if(repo_user.findOne(user.getId()).isPresent())
            user = repo_user.findOne(user.getId()).get();

        User finalUser = user;
        user.getFriends().forEach(friend -> {
            if (color.get(friend.getId()) == 0) {
                parent.put(friend.getId(), finalUser.getId());
                distance.put(friend.getId(), distance.get(finalUser.getId()) + 1);
                dfs_visit_sociable(color, parent, distance, friend);
            }
        });
        color.put(user.getId(), 2);

    }

    public Iterable<User> showAllUsers(){
        return repo_user.findALL();
    }

    public Optional<User> findUser(Long id){
        return repo_user.findOne(id);
    }

    public Optional<Friendship> findFriendship(Long id1, Long id2){
        return repo_friendship.findOne(new Tuple<Long, Long>(id1, id2));
    }

    public Optional<User> addUser(String firstName, String lastName) throws NoSuchAlgorithmException {

        User user = new User(firstName, lastName);
        user.setId(nextUserID());
        v_user.validate(user);
        var op =repo_user.save(user);

        notifyObservers();

        return op;

    }

    public Optional<User> removeUser(Long id) {

        return repo_user.delete(id);

    }

    public Iterable<Friendship> showAllFriendships(){

        /*//debug
        repo_user.findALL().forEach(x-> { x.getFriends().forEach(System.out::println);
            System.out.println(); } );*/

        return repo_friendship.findALL();
    }

    public Optional<Friendship> addFriendship(Long id1, Long id2) throws NoSuchAlgorithmException {

        Friendship fr = new Friendship();
        fr.setId(new Tuple<>(id1, id2));

        v_friendship.validate(fr);

        if (repo_user.findOne(id1).isEmpty() || repo_user.findOne(id2).isEmpty())
            throw new NotFoundException("At least one of the users doesn't exist!\n");

        for (Friendship f : repo_friendship.findALL()) {
            if (f.getId().equals(fr.getId())) {
                return Optional.empty();
            }
        }

        return repo_friendship.save(fr);
    }

    public Optional<Friendship> removeFriendship(Long id1, Long id2) throws NoSuchAlgorithmException {

        return repo_friendship.delete(new Tuple<>(id1, id2));

    }

    public int communitiesNumber(){

        AtomicInteger comNr = new AtomicInteger();
        Map<Long, Integer> color = new HashMap<>();
        Map<Long, Long> parent = new HashMap<>();

        repo_user.findALL().forEach(x -> {
            color.put(x.getId(), 0);
            parent.put(x.getId(), 0L);
        });

        repo_user.findALL().forEach(user -> {
            if(color.get(user.getId()) == 0) {
                comNr.addAndGet(1);
                dfs_visit_nr_com(color, parent, user);
            }
        });

        return comNr.get();
    }



    public ArrayList<Long> mostSociable(){

        final Integer[] maximal_length = {0};
        ArrayList<Long> res = new ArrayList<>();

        Map<Long, Integer> color = new HashMap<>();
        Map<Long, Long> parent = new HashMap<>();
        Map<Long, Integer> distance = new HashMap<>();

        Map<Long, Integer> color2 = new HashMap<>();
        Map<Long, Long> parent2 = new HashMap<>();
        Map<Long, Integer> distance2 = new HashMap<>();

        repo_user.findALL().forEach(x -> {
            color.put(x.getId(), 0);
            parent.put(x.getId(), 0L);
            distance.put(x.getId(), 0);

            color2.put(x.getId(), 0);
            parent2.put(x.getId(), 0L);
            distance2.put(x.getId(), 0);

        });

        repo_user.findALL().forEach(user -> {
            if(color.get(user.getId()) == 0) {
                repo_user.findALL().forEach(x -> distance.put(x.getId(), 0));
                dfs_visit_sociable(color, parent, distance, user);

                User user_max = null;
                Integer dist_max = 0;

                for (User user1 : repo_user.findALL()) {
                    if (distance.get(user1.getId()) > dist_max) {
                        dist_max = distance.get(user1.getId());
                        user_max = user1;
                    }
                }

                repo_user.findALL().forEach(x -> distance2.put(x.getId(), 0));

                if(user_max == null)
                    user_max = user;

                dfs_visit_sociable(color2, parent2, distance2, user_max);

                for (User user1 : repo_user.findALL()) {
                    if (distance2.get(user1.getId()) > maximal_length[0]) {
                        maximal_length[0] = distance2.get(user1.getId());
                        res.clear();

                        for (User user2 : repo_user.findALL()) {
                            if (distance2.get(user2.getId()) > 0) {
                                res.add(user2.getId());
                            }
                        }

                        res.add(user_max.getId());
                    }

                }
            }
        });

        return res;
    }

    public List<String> friendsFromMonth(Long id, int month){

        var op =  repo_user.findOne(id);
        if(op.isPresent()){
            ArrayList<User> frs = (ArrayList<User>) op.get().getFriends();
            return frs.stream()
                    .filter(x -> month ==  repo_friendship.findOne(new Tuple<>(id, x.getId())).get().getFriendsFrom().getMonthValue())
                    .map(x -> {
                        Friendship fr = repo_friendship.findOne(new Tuple<>(id, x.getId())).get();
                        return x.getLastName() + " | " + x.getFirstName() + " | " + fr.getFriendsFrom().toString();
                    })
                    .toList();
        }

        return null;
    }

    public Optional<User> updateUser(Long id, String firstName, String lastName, String password) throws NoSuchAlgorithmException {

        User user = new User(firstName, lastName);
        user.setId(id);
        user.setPassword(password);

        v_user.validate(user);

        return repo_user.update(user);
    }

    public Iterable<FriendshipRequest> showAllFriendshipRequests(){
        return repo_friendshipreq.findALL();
    }

    public Optional<FriendshipRequest> addFriendshipRequest(Long id1, Long id2){

        FriendshipRequest fr_r = new FriendshipRequest();
        fr_r.setId(new Tuple<>(id1, id2));

        v_friendshipreq.validate(fr_r);

        var op = repo_friendshipreq.findOne(new Tuple<>(id1, id2));
        if(op.isPresent())
            throw new FoundException("The request is already pending!");

        var op2 = repo_friendship.findOne(new Tuple<>(id1, id2));
        if(op2.isPresent())
            throw new FoundException("This user is already in your friend list!");

        return repo_friendshipreq.save(fr_r);

    }

    public Optional<FriendshipRequest> updateFriendshipRequest(Long id1, Long id2, String status){

        if(!status.equals("accepted") && !status.equals("declined"))
            throw new IllegalArgumentException("Status must be either 'accepted' or 'declined'!");

        FriendshipRequest fr_r = new FriendshipRequest();
        fr_r.setId(new Tuple<>(id1, id2));
        fr_r.setStatus(status);

        return repo_friendshipreq.update(fr_r);
    }

    public Optional<Message> findMessage(Long id){
        return repo_message.findOne(id);
    }
    public Iterable<Message> showMessagesBetweenTwo(Long id1, Long id2){

        Iterable<Message> messages = repo_message.findALL();

        return StreamSupport.stream(messages.spliterator(), false)
                .filter(x -> x.getFrom().equals(id1) && x.getTo().contains(id2) || x.getFrom().equals(id2) && x.getTo().contains(id1))
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    public void addMessage(Long from, List<Long> to, String text, Long is_reply_to){

        Message message = new Message(from, to, text);
        message.setIs_reply_to(is_reply_to);

        repo_message.save(message);

        notifyObservers();
    }

    public Page<User> showUsersPage(PagingInformation pagingInfo){

        return repo_user.findAll(pagingInfo);

    }

    public Page<User> showFriendsPage(PagingInformation pagingInfo, User user){

        return new PageObject<>(pagingInfo,
                repo_user.findOne(user.getId(), pagingInfo).get().getFriends().stream());

    }

    public Page<User> showFriendRequestsPage(PagingInformation pagingInfo, User user){

        return new PageObject<>(pagingInfo, repo_friendshipreq.findAllPendingPage(pagingInfo, user).getContent()
                .map(x -> repo_user.findOne(x.getId().getLeft()).get()));
    }

    public Page<Message> showMessagesPageBetweenTwo(PagingInformation pagingInfo, User user1, User user2){
        return new PageObject<>(pagingInfo, repo_message.findAllBetweenTwo(pagingInfo, user1, user2).getContent().sorted(Comparator.comparing(Message::getDate)));
    }

    @Override
    public void addObserver(Observer o) {

        observers.add(o);

    }

    @Override
    public void removeObserver(Observer o) {

        observers.remove(o);

    }

    @Override
    public void notifyObservers() {

        observers.forEach(Observer::update);

    }
}
