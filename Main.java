import java.util.*;

class Service {
    int firstHourPrice;
    int additionalHourPrice;

    Service(int firstHourPrice, int additionalHourPrice) {
        this.firstHourPrice = firstHourPrice;
        this.additionalHourPrice = additionalHourPrice;
    }
}

class UsageSession {
    String userId;
    long startTime;
    long endTime;

    UsageSession(String userId) {
        this.userId = userId;
        this.startTime = System.currentTimeMillis();
    }
}

class Resource {
    String resourceId;
    String name;
    int capacity;
    Service service;
    List<UsageSession> activeSessions = new ArrayList<>();

    Resource(String resourceId, String name, int capacity, Service service) {
        this.resourceId = resourceId;
        this.name = name;
        this.capacity = capacity;
        this.service = service;
    }
}

class UsageBillingSystem {

    Map<String, Resource> resources = new HashMap<>();

    
    void addResource(Resource resource) {
        resources.put(resource.resourceId, resource);
    }

    
    boolean startUsage(String resourceId, String userId) {
        Resource resource = resources.get(resourceId);

        if (resource.activeSessions.size() >= resource.capacity) {
            System.out.println(" Resource capacity full. Access denied.");
            return false;
        }

        resource.activeSessions.add(new UsageSession(userId));
        System.out.println("Usage started for user " + userId);
        return true;
    }

    
    void stopUsage(String resourceId, String userId) {
        Resource resource = resources.get(resourceId);
        UsageSession session = null;

        for (UsageSession s : resource.activeSessions) {
            if (s.userId.equals(userId)) {
                session = s;
                break;
            }
        }

        if (session == null) {
            System.out.println(" No active session found.");
            return;
        }

        session.endTime = System.currentTimeMillis();
        resource.activeSessions.remove(session);

        generateBill(resource, session);
    }

    
    void generateBill(Resource resource, UsageSession session) {
        long durationMs = session.endTime - session.startTime;
        long hours = (long) Math.ceil(durationMs / (1000.0 * 60 * 60));

        int amount = resource.service.firstHourPrice;
        if (hours > 1) {
            amount += (hours - 1) * resource.service.additionalHourPrice;
        }

        System.out.println("----- BILL -----");
        System.out.println("User: " + session.userId);
        System.out.println("Resource: " + resource.name);
        System.out.println("Hours Used: " + hours);
        System.out.println("Amount: ₹" + amount);
        System.out.println("----------------");
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {

        UsageBillingSystem system = new UsageBillingSystem();

        Service meetingRoomService = new Service(30, 10);
        Resource meetingRoom = new Resource("R1", "Meeting Room", 2, meetingRoomService);

        system.addResource(meetingRoom);

        system.startUsage("R1", "User1");
        Thread.sleep(3000); 

        system.startUsage("R1", "User2");
        Thread.sleep(2000);

        system.stopUsage("R1", "User1");
        system.stopUsage("R1", "User2");
    }
}
