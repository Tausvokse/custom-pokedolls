package com.fossilcollector.pokedolls.block;

import com.fossilcollector.pokedolls.entity.PokedollBlockEntity;
import com.fossilcollector.pokedolls.registry.PokedollSpecies;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PokedollBlock extends BlockWithEntity implements Waterloggable {
    public static final IntProperty ROTATION = Properties.ROTATION;
    public static final BooleanProperty SHINY = BooleanProperty.of("shiny");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty GLOWING = BooleanProperty.of("glowing");
    public static final EnumProperty<PokedollSize> SIZE = EnumProperty.of("size", PokedollSize.class);

    public static java.util.function.Consumer<BlockPos> SCREEN_OPENER = pos -> {};

    public static final MapCodec<PokedollBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            createSettingsCodec(),
            StringIdentifiable.createCodec(PokedollSpecies::values).fieldOf("species").forGetter(PokedollBlock::getSpecies)
        ).apply(instance, PokedollBlock::new)
    );

    private final PokedollSpecies species;

    public PokedollBlock(Settings settings, PokedollSpecies species) {
        super(settings);
        this.species = species;
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(ROTATION, 0)
            .with(SHINY, false)
            .with(WATERLOGGED, false)
            .with(SIZE, PokedollSize.NORMAL)
            .with(GLOWING, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public PokedollSpecies getSpecies() {
        return this.species;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, SHINY, WATERLOGGED, SIZE, GLOWING);
    }

    public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2, 0, 2, 14, 16, 14);

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        int rot = MathHelper.floor((double)((ctx.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15;
        boolean isShiny = ctx.getStack().getItem().toString().contains("shiny");
        return this.getDefaultState()
            .with(ROTATION, rot)
            .with(SHINY, isShiny)
            .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
            .with(SIZE, PokedollSize.NORMAL)
            .with(GLOWING, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient() && placer != null && placer.isPlayer()) {
            SCREEN_OPENER.accept(pos);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PokedollBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND && stack.isOf(com.fossilcollector.pokedolls.registry.ModBlocks.POKEDOLL_CHISEL)) {
            if (world.isClient()) {
                SCREEN_OPENER.accept(pos);
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
