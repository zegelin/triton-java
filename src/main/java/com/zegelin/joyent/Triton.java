package com.zegelin.joyent;

import java.util.List;
import java.util.concurrent.Future;

public interface Triton {

    Future<List<Package>> packages();
    Future<Package> getPackage(final IdOrName<Package> packageIdOrName);

    Future<List<Image>> images();
    Future<Image> getImage(final Id<Image> imageId);

    Future<List<Machine>> machines();
    CreateMachineRequest.Builder createMachine(final IdOrName<Package> pkg, final IdOrName<Image> image);
    Future<Machine> machineWithId(final Id<Machine> machineId);
//    Future<Void> deleteMachine(final App.MachineId machine);


}
