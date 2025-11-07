package org.ivcode.kcomfy.model.nodes

/**
 * Sampler algorithm identifiers used by the sampler node.
 *
 * Each enum constant holds the canonical key string used by the server/UI.
 */
@Suppress("unused")
enum class SamplerName(val key: String) {
    /** Euler */
    EULER("euler"),

    /** Euler (CFG++) */
    EULER_CFG_PP("euler_cfg_pp"),

    /** Euler Ancestral */
    EULER_ANCESTRAL("euler_ancestral"),

    /** Euler Ancestral (CFG++) */
    EULER_ANCESTRAL_CFG_PP("euler_ancestral_cfg_pp"),

    /** Heun */
    HEUN("heun"),

    /** Heun++ (2) */
    HEUNPP2("heunpp2"),

    /** DPM2 */
    DPM_2("dpm_2"),

    /** DPM2 Ancestral */
    DPM_2_ANCESTRAL("dpm_2_ancestral"),

    /** LMS */
    LMS("lms"),

    /** DPM Fast */
    DPM_FAST("dpm_fast"),

    /** DPM Adaptive */
    DPM_ADAPTIVE("dpm_adaptive"),

    /** DPM++ 2S Ancestral */
    DPMPP_2S_ANCESTRAL("dpmpp_2s_ancestral"),

    /** DPM++ 2S Ancestral (CFG++) */
    DPMPP_2S_ANCESTRAL_CFG_PP("dpmpp_2s_ancestral_cfg_pp"),

    /** DPM++ SDE */
    DPMPP_SDE("dpmpp_sde"),

    /** DPM++ SDE (GPU) */
    DPMPP_SDE_GPU("dpmpp_sde_gpu"),

    /** DPM++ 2M */
    DPMPP_2M("dpmpp_2m"),

    /** DPM++ 2M (CFG++) */
    DPMPP_2M_CFG_PP("dpmpp_2m_cfg_pp"),

    /** DPM++ 2M SDE */
    DPMPP_2M_SDE("dpmpp_2m_sde"),

    /** DPM++ 2M SDE (GPU) */
    DPMPP_2M_SDE_GPU("dpmpp_2m_sde_gpu"),

    /** DPM++ 2M SDE Heun */
    DPMPP_2M_SDE_HEUN("dpmpp_2m_sde_heun"),

    /** DPM++ 2M SDE Heun (GPU) */
    DPMPP_2M_SDE_HEUN_GPU("dpmpp_2m_sde_heun_gpu"),

    /** DPM++ 3M SDE */
    DPMPP_3M_SDE("dpmpp_3m_sde"),

    /** DPM++ 3M SDE (GPU) */
    DPMPP_3M_SDE_GPU("dpmpp_3m_sde_gpu"),

    /** DDPM */
    DDPM("ddpm"),

    /** LCM */
    LCM("lcm"),

    /** iPNDM */
    IPNDM("ipndm"),

    /** iPNDM v */
    IPNDM_V("ipndm_v"),

    /** DEIS */
    DEIS("deis"),

    /** ResMultistep */
    RES_MULTISTEP("res_multistep"),

    /** ResMultistep (CFG++) */
    RES_MULTISTEP_CFG_PP("res_multistep_cfg_pp"),

    /** ResMultistep Ancestral */
    RES_MULTISTEP_ANCESTRAL("res_multistep_ancestral"),

    /** ResMultistep Ancestral (CFG++) */
    RES_MULTISTEP_ANCESTRAL_CFG_PP("res_multistep_ancestral_cfg_pp"),

    /** Gradient Estimation */
    GRADIENT_ESTIMATION("gradient_estimation"),

    /** Gradient Estimation (CFG++) */
    GRADIENT_ESTIMATION_CFG_PP("gradient_estimation_cfg_pp"),

    /** ER SDE */
    ER_SDE("er_sde"),

    /** Seeds 2 */
    SEEDS_2("seeds_2"),

    /** Seeds 3 */
    SEEDS_3("seeds_3"),

    /** SA Solver */
    SA_SOLVER("sa_solver"),

    /** SA Solver (PECE) */
    SA_SOLVER_PECE("sa_solver_pece");

    override fun toString(): String = key
}


/**
 * Scheduler identifiers used by scheduling strategies.
 *
 * Each enum constant stores the canonical key string used by the server/UI.
 */
@Suppress("unused")
enum class Scheduler(val key: String) {
    /** Simple scheduler */
    SIMPLE("simple"),

    /** SGM Uniform scheduler */
    SGM_UNIFORM("sgm_uniform"),

    /** Karras scheduler (Karras noise schedule) */
    KARRAS("karras"),

    /** Exponential scheduler */
    EXPONENTIAL("exponential"),

    /** UniPC / DDIM uniform variant */
    DDIM_UNIFORM("ddim_uniform"),

    /** Beta scheduler */
    BETA("beta"),

    /** Normal scheduler */
    NORMAL("normal"),

    /** Linear-Quadratic scheduler */
    LINEAR_QUADRATIC("linear_quadratic"),

    /** KL-optimal scheduler */
    KL_OPTIMAL("kl_optimal");

    override fun toString(): String = key
}


/**
 * Control modes for post-generation adjustments.
 *
 * Each enum constant holds the canonical key string used by the server/UI.
 */
@Suppress("unused")
enum class ControlAfterGenerateMode(val key: String) {
    /** Fixed value */
    FIXED("fixed"),

    /** Incremental change */
    INCREMENT("increment"),

    /** Decremental change */
    DECREMENT("decrement"),

    /** Randomize value */
    RANDOMIZE("randomize");

    override fun toString(): String = key
}
