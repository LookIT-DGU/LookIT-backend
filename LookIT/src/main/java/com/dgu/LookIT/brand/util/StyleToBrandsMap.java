package com.dgu.LookIT.brand.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class StyleToBrandsMap {

    public static final Map<String, List<BrandInfo>> STYLE_BRAND_MAP = Map.ofEntries(
            entry("오피스룩", List.of(
                    new BrandInfo("MIXXO", "https://mixxo.com"),
                    new BrandInfo("Mind Bridge", "https://www.mindbridge.com.my/"),
                    new BrandInfo("CLAVIS", "https://clavisfashionhub.com")
            )),
            entry("미니멀룩", List.of(
                    new BrandInfo("Our Legacy", "https://www.ourlegacy.com"),
                    new BrandInfo("Maison Margiela", "https://www.maisonmargiela.com/en-us/"),
                    new BrandInfo("Matin Kim", "https://matinkim.shop"),
                    new BrandInfo("COS", "https://www.cos.com/en_usd/index.html"),
                    new BrandInfo("Andersson Bell", "https://adsb.co.kr")
            )),
            entry("시크룩", List.of(
                    new BrandInfo("Atelier Nine", "https://www.nain.co.kr"),
                    new BrandInfo("A.P.C.", "https://www.apc.fr"),
                    new BrandInfo("Acne Studios", "https://www.acnestudios.com")
            )),
            entry("모던룩", List.of(
                    new BrandInfo("Insilence Women", "https://www.musinsa.com/brands/insilence"),
                    new BrandInfo("Nick & Nicole", "https://nicknnicole.co.kr/"),
                    new BrandInfo("Draw Fit", "https://www.draw-fit.com")
            )),
            entry("컨템포러리룩", List.of(
                    new BrandInfo("Draw Fit Women", "https://www.draw-fit.com"),
                    new BrandInfo("Musent", "https://mucent.kr/"),
                    new BrandInfo("Avandress", "https://avandress.com/")
            )),
            entry("프레피룩", List.of(
                    new BrandInfo("Polo Ralph Lauren", "https://www.ralphlauren.com"),
                    new BrandInfo("Lacoste", "https://www.lacoste.com"),
                    new BrandInfo("Maison Kitsuné", "https://www.maisonkitsune.com"),
                    new BrandInfo("Tommy Hilfiger", "https://www.tommy.com")
            )),
            entry("로맨틱룩", List.of(
                    new BrandInfo("Letter From Moon", "https://www.letterfrommoon.com"),
                    new BrandInfo("Roem", "https://www.roem.com"),
                    new BrandInfo("Lookast", "https://lookast.com/")
            )),
            entry("러블리룩", List.of(
                    new BrandInfo("TwEE", "https://www.twee.co.kr"),
                    new BrandInfo("KIRSH", "https://www.kirsh.co.kr"),
                    new BrandInfo("Maybe Baby", "https://www.maybe-baby.co.kr"),
                    new BrandInfo("Rolarola", "https://www.rolarola.com")
            )),
            entry("코티지코어룩", List.of(
                    new BrandInfo("Bernice", "https://berenice.net/gb/"),
                    new BrandInfo("Slow And", "https://www.slowand.com"),
                    new BrandInfo("Perbit", "https://m.perbit.co.kr/")
            )),
            entry("보헤미안룩", List.of(
                    new BrandInfo("Thursday Island", "https://www.thursdayisland.co.kr"),
                    new BrandInfo("Chloé", "https://www.chloe.com"),
                    new BrandInfo("Isabel Marant", "https://www.isabelmarant.com")
            )),
            entry("드뮤어룩", List.of(
                    new BrandInfo("Lemaire", "https://www.lemaire.fr"),
                    new BrandInfo("The Row", "https://www.therow.com"),
                    new BrandInfo("ZARA", "https://www.zara.com"),
                    new BrandInfo("Fromwhere", "https://fromwhere.co.kr/")
            )),
            entry("오버핏룩", List.of(
                    new BrandInfo("Marie Claire", "www.marieclairekorea.com"),
                    new BrandInfo("Mardi Mercredi", "https://www.mardimercredi.com"),
                    new BrandInfo("Coor", "https://coor.kr/")
            )),
            entry("젠더리스룩", List.of(
                    new BrandInfo("Carhartt", "https://www.carhartt.com"),
                    new BrandInfo("Venhit", "https://venhit.co.kr/"),
                    new BrandInfo("Calvin Klein", "https://www.calvinklein.com"),
                    new BrandInfo("SPAO", "https://www.spao.com")
            )),
            entry("빈티지룩", List.of(
                    new BrandInfo("A DAUL", "https://a-daul.com/"),
                    new BrandInfo("Citypopz", "https://citypopz.com/"),
                    new BrandInfo("VITALSIGN", "https://vitalsign.kr/"),
                    new BrandInfo("SETUPEXE", "https://setup-exe.com/")
            ))
    );

    @Getter
    @AllArgsConstructor
    public static class BrandInfo {
        private String name;
        private String url;
    }
}