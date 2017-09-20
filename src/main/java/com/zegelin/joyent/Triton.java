package com.zegelin.joyent;

import java.util.List;
import java.util.concurrent.Future;

@SuppressWarnings("unused")
public interface Triton {
    Future<Account> account();

    Future<List<Package>> packages();
    Future<Package> packageWithIdOrName(final IdOrName<Package> packageIdOrName);

    Future<List<Image>> images();
    Future<Image> imageForId(final Id<Image> imageId);

    Future<List<Machine>> machines();
    Future<Machine> machineWithId(final Id<Machine> machineId);
    CreateMachineRequest.Builder createMachine(final IdOrName<Package> pkg, final IdOrName<Image> image);
    Future<Void> deleteMachine(final Id<Machine> machineId);
    Future<Void> stopMachine(final Id<Machine> machineId);
    Future<Void> startMachine(final Id<Machine> machineId);
    Future<Void> rebootMachine(final Id<Machine> machineId);
    Future<Void> resizeMachine(final Id<Machine> machineId, final IdOrName<Package> newPackage);
    Future<Void> renameMachine(final Id<Machine> machineId, final String newName);
}
