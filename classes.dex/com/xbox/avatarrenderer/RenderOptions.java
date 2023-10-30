package com.xbox.avatarrenderer;

public class RenderOptions {
    public Boolean bAllowMipmaps;
    public Boolean bCullDegenerateTrianglesAndUnusedVertecies;
    public Boolean bCullTextures;
    public Boolean bDoPerVertexShading;
    public Boolean bHarwareSkinning;
    public Boolean bSingleBoneOptimization;
    public Boolean bUseColorPerVertex;
    public Boolean bUsePackedBoneMatricies;
    public Boolean bUseRimLighting;

    public RenderOptions() {
        this.bUseColorPerVertex = Boolean.valueOf(true);
        this.bDoPerVertexShading = Boolean.valueOf(true);
        this.bUseRimLighting = Boolean.valueOf(true);
        this.bHarwareSkinning = Boolean.valueOf(true);
        this.bAllowMipmaps = Boolean.valueOf(true);
        this.bCullDegenerateTrianglesAndUnusedVertecies = Boolean.valueOf(true);
        this.bUsePackedBoneMatricies = Boolean.valueOf(true);
        this.bCullTextures = Boolean.valueOf(true);
        this.bSingleBoneOptimization = Boolean.valueOf(true);
        this.bUseColorPerVertex = Boolean.valueOf(true);
        this.bDoPerVertexShading = Boolean.valueOf(true);
        this.bUseRimLighting = Boolean.valueOf(true);
        this.bHarwareSkinning = Boolean.valueOf(true);
        this.bAllowMipmaps = Boolean.valueOf(true);
        this.bCullDegenerateTrianglesAndUnusedVertecies = Boolean.valueOf(true);
        this.bUsePackedBoneMatricies = Boolean.valueOf(true);
        this.bCullTextures = Boolean.valueOf(true);
        this.bSingleBoneOptimization = Boolean.valueOf(true);
    }
}
