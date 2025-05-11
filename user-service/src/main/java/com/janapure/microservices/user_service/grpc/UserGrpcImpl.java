package com.janapure.microservices.user_service.grpc;

import com.janapure.microservices.proto.*;
import com.janapure.microservices.user_service.repository.UserAddressRepo;
import com.janapure.microservices.user_service.repository.UserCredentialRepo;
import com.janapure.microservices.user_service.repository.UserRepo;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class UserGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserAddressRepo userAddressRepo;

    @Autowired
    private UserCredentialRepo userCredentialRepo;

//    public UserGrpcImpl(UserRepo userRepo, UserAddressRepo userAddressRepo, UserCredentialRepo userCredentialRepo) {
//        this.userRepo = userRepo;
//        this.userAddressRepo = userAddressRepo;
//        this.userCredentialRepo = userCredentialRepo;
//    }


    @Override
    public void getUserInfo(UserIdRequest request, StreamObserver<UserInfoResponse> responseObserver) {
        UserInfoResponse response = UserInfoResponse.newBuilder()
                .setUserId(request.getUserId())
                .setFirstName("John")
                .setLastName("Doe")
                .setMobileNo("1234567890")
                .addRoles("USER")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAddress(UserIdRequest request, StreamObserver<UserAddressResponse> responseObserver) {

        UserAddressResponse response = UserAddressResponse.newBuilder()
                .setUserId(request.getUserId())
                .addAddresses(
                        com.janapure.microservices.proto.Address.newBuilder()
                                .setAddressId("1")
                                .setAddressLine1("Line 1")
                                .setAddressLine2("Line 2")
                                .setCity("City")
                                .setState("State")
                                .setPostalCode("123456")
                                .setCountry("Country")
                                .build()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateUser(UserIdRequest request, StreamObserver<ValidateUserResponse> responseObserver) {

        System.out.println("Validating user: " + request.getUserId());
        boolean isValid = userCredentialRepo.existsByUsername(request.getUserId());
        ValidateUserResponse response = ValidateUserResponse.newBuilder()
                .setIsValid(isValid)
                .setUserId(request.getUserId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
