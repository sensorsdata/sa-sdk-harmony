package com.sensorsdata.analytics.harmony.sdk.common.utils;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.agp.components.Component;
import ohos.app.AbilityContext;
import ohos.app.Context;
import ohos.utils.zson.ZSONObject;

public class SAEventUtils {
    public static ZSONObject getComponentProperties(Component component){
        if(component == null){
            return null;
        }
        Context context = component.getContext();
        if(context instanceof AbilitySlice){
            SALog.i("view---Click","AbilitySlice");
        }else if(context instanceof Fraction){
            SALog.i("view---Click", "Fraction");
        }else if(context instanceof Ability){
            SALog.i("view---Click","Ability");
        }
        return null;
    }

    public static ZSONObject getAbilityProperties(AbilityContext abilityContext){
        if(abilityContext == null){
            return null;
        }
        if(abilityContext instanceof AbilitySlice){
            SALog.i("view---Screen", "AbilitySlice"  + SAAppInfoUtils.getAbilityLabel(abilityContext));
        }else if(abilityContext instanceof Ability){
            SALog.i("view---Screen","Ability"  + SAAppInfoUtils.getAbilityLabel(abilityContext));
        }
        return null;
    }
}
